package it.unibo.ing.stormsmacs.topologies.reliable.bolts.OpenStack

import java.net.URL
import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource}
import org.openstack.clients.ceilometer.v2.{ICeilometerClient, CeilometerClient}
import storm.scala.dsl.additions.Logging
import storm.scala.dsl.StormBolt

import scala.collection.mutable

/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Storm Bolt that lists all the samples of a given Resource that will be monitored
 */
class OpenStackSampleBolt(pollTime: Long)
  extends StormBolt(List("NodeName", "GraphName", "Resource", "Sample"))
  with Logging {
  private val _clients: mutable.Map[String, ICeilometerClient] = _
  override def execute(input: Tuple) = input matchSeq {
      case Seq(node: OpenStackNodeConf, date: Date, resource: Resource) =>
        val cclient = getClient(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
        val start = new Date(date.getTime - pollTime)
        val maybeSamples = cclient.tryGetSamplesOfResource(resource.resource_id, start, date)
        maybeSamples match {
          case Some(Nil) =>
            input.ack //no samples for this resource, we just ack the tuple
          case Some(samples: Seq[Sample]) => {
            for (s <- samples)
              using anchor input emit(node, date, resource, s)
            input.ack
          }
          case None =>
            logger.error(s"cannot get samples of ${resource.resource_id} from $start to $date")
            input.fail //if we get None as a result, something bad happened, we need to replay the tuple
        }
  }
  private def getClient(ceilometerUrl : URL, keystoneUrl : URL, tenantName : String,  username : String, password : String, connectTimeout: Int, readTimeout: Int) : ICeilometerClient = {
    val key = ceilometerUrl+tenantName+username+password
    if (!(_clients contains key))
      _clients(key) = new CeilometerClient(ceilometerUrl, keystoneUrl, tenantName, username, password, connectTimeout, readTimeout)
    _clients(key)
  }
}