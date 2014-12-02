package it.unibo.ing.stormsmacs.topologies.bolts

import java.io.IOException
import java.util.Date
import backtype.storm.tuple.Tuple
import org.openstack.api.restful.keystone.v2.{TokenProvider, KeystoneTokenProvider}
import it.unibo.ing.stormsmacs.conf.OpenStackNode
import org.openstack.clients.ceilometer.v2.CeilometerClient
import spray.json.DeserializationException
import spray.json.JsonParser.ParsingException
import storm.scala.dsl.{StormBolt, Logging}


/**
 * @author Antonio Murgia
 */

class OpenStackNodeClientBolt(node : OpenStackNode)
  extends StormBolt(List("stat"))
  with Logging{
  private var tokenProvider : TokenProvider = _
  private var cclient : CeilometerClient = _
  setup{
    tokenProvider = KeystoneTokenProvider.getInstance(node.keystoneUrl, node.tenantName, node.username, node.password)
    cclient = new CeilometerClient(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
  }

  def execute(t : Tuple) = t matchSeq {
    case Seq(graphName: Date) => {
      try{
        cclient.listMeters.foreach(using.anchor(t).emit(graphName, _))
      }
      catch{
        case e : IOException => {
          logger.error(e.getMessage)
        }
        case e : ParsingException => {
          logger.error(e.getMessage)
        }
        case e : DeserializationException => {
          logger.error(e.getMessage)
        }
      }
    }
      t.ack
  }
}