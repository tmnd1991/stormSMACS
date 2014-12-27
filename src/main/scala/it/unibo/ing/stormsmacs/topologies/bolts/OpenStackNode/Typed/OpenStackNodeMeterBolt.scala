package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import org.openstack.api.restful.ceilometer.v2.elements.{Statistics, Meter}
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.TypedBolt
import storm.scala.dsl.Logging

/**
 * Created by tmnd91 on 22/12/14.
 */
class OpenStackNodeMeterBolt
  extends TypedBolt[(OpenStackNodeConf, Date, Meter),(OpenStackNodeConf, Date, String, Statistics)](
    "NodeName", "GraphName", "MeterName", "Statistics")
  with Logging{
  override def typedExecute(t: (OpenStackNodeConf, Date, Meter), st : Tuple) = {
    try{
      val cclient = CeilometerClient.getInstance(t._1.ceilometerUrl, t._1.keystoneUrl, t._1.tenantName, t._1.username, t._1.password, t._1.connectTimeout, t._1.readTimeout)
      val start = new Date(t._2.getTime - t._1.duration)
      for(stat <- cclient.getStatistics(t._3, start, t._2))
      using anchor st emit(t._1, t._2, t._3.name, stat)
      st.ack
    }
    catch{
      case e : Throwable =>{
        logger.error(e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }
}
