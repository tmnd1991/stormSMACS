package it.unibo.ing.stormsmacs.topologies.unreliable.bolts.OpenStack

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import org.openstack.api.restful.ceilometer.v2.elements.{Resource, Sample}
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.StormBolt
import storm.scala.dsl.additions.Logging

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Storm Bolt that lists all the samples of a given Resource that will be monitored
 */
class OpenStackSampleBolt(pollTime: Long)
  extends StormBolt(List("NodeName", "GraphName", "Resource", "Sample"))
  with Logging {
  override def execute(input: Tuple) = {
    input matchSeq {
      case Seq(node: OpenStackNodeConf, date: Date, resource: Resource) =>
        val cclient = CeilometerClient.getInstance(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
        val start = new Date(date.getTime - pollTime)
        cclient.tryGetSamplesOfResource(resource.resource_id, start, date) match {
          case Success(Nil) => logger.info("ack - no samples " + date)
          case Success(samples: Seq[Sample]) =>
            for (s <- samples)
              _collector.synchronized(using no anchor emit(node, date, resource, s))
          case Failure(e) => logger.info(e.getMessage, e)
        }
    }
  }
}