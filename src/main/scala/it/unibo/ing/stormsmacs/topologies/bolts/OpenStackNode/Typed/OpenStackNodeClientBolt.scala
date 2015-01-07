package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

/**
 * Created by tmnd91 on 22/12/14.
 */

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import org.openstack.api.restful.ceilometer.v2.elements.Meter
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.{Logging, TypedBolt}
import it.unibo.ing.utils.PimpMyLib._

class OpenStackNodeClientBolt(node : OpenStackNodeConf)
  extends TypedBolt[Tuple1[Date],(OpenStackNodeConf, Date, Meter)]("NodeName","GraphName","Meter")
  with Logging{
  private var cclient : CeilometerClient = _
  setup{
    cclient = CeilometerClient.getInstance(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
  }
  override def typedExecute(t: Tuple1[Date], st : Tuple) = {
    val meters = cclient.tryListMeters
    if (meters.isDefined){
      for (meter <- meters.get.distinctBy(_.name))
        using anchor st emit(node, t._1, meter)
      st.ack
    }
    else
      st.fail
  }
}


