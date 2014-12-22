package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

/**
 * Created by tmnd91 on 22/12/14.
 */

import java.util.Date

import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.Typed.TimerSpout
import org.openstack.api.restful.ceilometer.v2.elements.Meter
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.{TypedTopologyBuilder, Logging, TypedBolt}

class OpenStackNodeClientBolt(node : OpenStackNodeConf)
  extends TypedBolt[Tuple1[Date],(OpenStackNodeConf, Date, Meter)]("NodeName","GraphName","Meter")
  with Logging{
  private var cclient : CeilometerClient = _

  setup{
    cclient = CeilometerClient.getInstance(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
  }
  override def typedExecute(t: Tuple1[Date]): Seq[(OpenStackNodeConf, Date, Meter)] = {
    cclient.listMeters.map((node, t._1, _))
  }
}


