package it.unibo.ing.stormsmacs.topologies.bolts

import java.io.IOException
import java.util.Date
import backtype.storm.tuple.Tuple
import org.openstack.api.restful.keystone.v2.{TokenProvider, KeystoneTokenProvider}
import org.openstack.api.restful.ceilometer.v2.elements.Meter
import org.openstack.clients.ceilometer.v2.CeilometerClient
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import spray.json.DeserializationException
import spray.json.JsonParser.ParsingException
import storm.scala.dsl.{StormBolt, Logging}


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
            using anchor t emit(node, graphName, _)
          t.ack
        }
      }
    }
  }
}