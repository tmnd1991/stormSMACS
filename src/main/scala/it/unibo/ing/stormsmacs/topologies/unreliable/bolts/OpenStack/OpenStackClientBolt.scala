package it.unibo.ing.stormsmacs.topologies.unreliable.bolts.OpenStack

/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Storm Bolt that lists all the resources that will be monitored
 */

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import org.openstack.api.restful.ceilometer.v2.elements.Resource
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.StormBolt
import storm.scala.dsl.additions.Logging

class OpenStackClientBolt(node : OpenStackNodeConf)
  extends StormBolt(List("NodeName","GraphName","Resource"))
  with Logging{
  private var cclient : CeilometerClient = _
  setup{
    cclient = CeilometerClient.getInstance(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
  }
  shutdown{
    cclient = null
  }
  override def execute(t : Tuple) : Unit = {
    t matchSeq{
      case Seq(date: Date)=>{
        cclient.tryListAllResources match{
          case Some(Nil) => logger info ("ack no samples " + date)
          case Some(res : Seq[Resource]) => {
            for (r <- res)
              using anchor t emit(node, date, r)
          }
          case _ => logger info ("fail " + date)
        }
      }
    }
  }
}


