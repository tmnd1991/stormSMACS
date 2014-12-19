package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.{Logging, StormBolt}


/**
 * @author Antonio Murgia
 * @version 12/12/2014
 */

class OpenStackNodeClientBolt(node : OpenStackNodeConf)
  extends StormBolt(List("NodeName","GraphName","Meter"))
  with Logging{

  private var cclient : CeilometerClient = _

  setup{
    cclient = CeilometerClient.getInstance(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
  }

  def execute(t : Tuple) = {
    t.matchSeq {
      case Seq(graphName: Date) => {
        val meters = cclient.tryListMeters
        if(meters.isDefined){
          for (m <- meters)
            using anchor t emit(node, graphName, m)
          t.ack
        }
      }
    }
  }
}