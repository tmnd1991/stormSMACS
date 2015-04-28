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

  override def execute(input: Tuple) = input matchSeq {
    case Seq(node: OpenStackNodeConf, date: Date, resource: Resource) =>
      val cclient = CeilometerClient.getInstance(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
      val start = new Date(date.getTime - pollTime)
      Future{
        cclient.tryGetSamplesOfResource(resource.resource_id, start, date) match {
          case Success(Nil) => _collector.synchronized(input.ack)
          case Success(samples: Seq[Sample]) =>
            for (s <- samples)
              _collector.synchronized(using anchor input emit(node, date, resource, s))
            _collector.synchronized(input.ack)
          case Failure(e) =>
            logger.error(e.getMessage,e)
            _collector.synchronized(input.fail)
        }
      }
  }
}
