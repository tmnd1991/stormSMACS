package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import java.net.{URL, URI}
import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, OpenStackNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackResourceData
import it.unibo.ing.utils._
import org.eclipse.jetty.client.{ContentExchange, HttpClient}
import org.eclipse.jetty.io.ByteArrayBuffer
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource}
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.TypedBolt
import storm.scala.dsl.Logging

import it.unibo.ing.stormsmacs.GraphNamer

/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Storm Bolt that lists all the samples of a given Resource that will be monitored
 */
class OpenStackNodeSampleBolt(pollTime: Long)
  extends TypedBolt[(OpenStackNodeConf, Date, Resource),(OpenStackNodeConf, Date, Resource, Sample)](
    "NodeName", "GraphName", "Resource", "Sample")
  with Logging{

  override def typedExecute(t: (OpenStackNodeConf, Date, Resource), st : Tuple) = {
    val cclient = CeilometerClient.getInstance(t._1.ceilometerUrl, t._1.keystoneUrl, t._1.tenantName, t._1.username, t._1.password, t._1.connectTimeout, t._1.readTimeout)
    val start = new Date(t._2.getTime - pollTime)
    cclient.tryGetSamplesOfResource(t._3.resource_id, start, t._2) match{
      case Some(Nil) => st.ack        //no samples for this resource, we just ack the tuple
      case Some(samples : Seq[Sample]) => {
        for (s <- samples) using anchor st emit(t._1, t._2, t._3, s)
        st.ack
      }
      case _ => st.fail            //if we get None as a result, something bad happened, we need to replay the tuple
    }
  }
}
