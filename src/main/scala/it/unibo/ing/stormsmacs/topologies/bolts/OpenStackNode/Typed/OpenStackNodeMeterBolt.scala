package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource, Statistics, Meter}
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.TypedBolt
import storm.scala.dsl.Logging

/**
 * @author Antonio Murgia
 * @version 22/12/14
 */
class OpenStackNodeMeterBolt(pollTime: Long)
  extends TypedBolt[(OpenStackNodeConf, Date, Resource),(OpenStackNodeConf, Date, Resource, Sample)](
    "NodeName", "GraphName", "Resource", "Sample")
  with Logging{
  override def typedExecute(t: (OpenStackNodeConf, Date, Resource), st : Tuple) = {
    val cclient = CeilometerClient.getInstance(t._1.ceilometerUrl, t._1.keystoneUrl, t._1.tenantName, t._1.username, t._1.password, t._1.connectTimeout, t._1.readTimeout)

    val start = new Date(t._2.getTime - pollTime)
    val samples = cclient.tryGetSamplesOfResource(t._3.resource_id, start, t._2)
    if (samples.isDefined){
      for(sample <- samples.get)
          using anchor st emit(t._1, t._2, t._3, sample)
        st.ack
    }
    else
      st.fail
  }
}
