package it.unibo.ing.stormsmacs.topologies.bolts

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import org.openstack.api.restful.ceilometer.v2.elements.{Statistics, Meter}
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.{Logging, StormBolt}

/**
 * Created by tmnd on 18/12/14.
 */
class OpenStackNodeMeterBolt()
  extends StormBolt(List("NodeName","GraphName","Meter"))
  with Logging{

  override def execute(t: Tuple) =
    t.matchSeq{
      case Seq(node : OpenStackNodeConf, graphName: Date, m : Meter) =>{
        val cclient = CeilometerClient.getInstance(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
        val start = new Date(graphName.getTime - node.duration)
        cclient.tryGetStatistics(m, start, graphName) match{
          case Some(stats : Seq[Statistics]) =>{
            for(stat <- stats)
              using anchor t emit(node, graphName, m.name, stat)
            t.ack
          }
        }
      }
   }
}
