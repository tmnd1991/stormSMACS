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
import org.openstack.clients.ceilometer.v2.{ICeilometerClient, CeilometerClient}
import storm.scala.dsl.StormBolt
import storm.scala.dsl.additions.Logging

import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

class OpenStackClientBolt(node : OpenStackNodeConf)
  extends StormBolt(List("NodeName","GraphName","Resource"))
  with Logging{
  private var cclient : ICeilometerClient = _
  setup{
    cclient = CeilometerClient.getInstance(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
  }
  shutdown{
    cclient = null
  }
  override def execute(t : Tuple) : Unit = {
    t matchSeq{
      case Seq(date: Date)=>
        Future{
          cclient.tryListAllResources match{
            case Success(Nil) => logger info ("ack no samples " + date)
            case Success(res : Seq[Resource]) =>
              for (r <- res)
                _collector.synchronized(using no anchor emit(node, date, r))
            case Failure(e) => logger.info(e.getMessage,e)
          }
        }
    }
  }
}


