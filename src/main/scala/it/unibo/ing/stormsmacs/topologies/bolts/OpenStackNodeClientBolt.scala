package it.unibo.ing.stormsmacs.topologies.bolts

import java.io.IOException
import java.util.Date
import backtype.storm.tuple.Tuple
import org.openstack.api.restful.keystone.v2.{TokenProvider, KeystoneTokenProvider}
import org.openstack.api.restful.ceilometer.v2.elements.Meter
import org.openstack.clients.ceilometer.v2.CeilometerClient
import it.unibo.ing.stormsmacs.conf.OpenStackNode
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
  private var meters : Seq[Meter] = _
  setup{
    tokenProvider = KeystoneTokenProvider.getInstance(node.keystoneUrl, node.tenantName, node.username, node.password)
    cclient = new CeilometerClient(node.ceilometerUrl, node.keystoneUrl, node.tenantName, node.username, node.password, node.connectTimeout, node.readTimeout)
    meters = cclient.listMeters
  }
  shutdown{
    cclient.shutdown()
  }
  def execute(t : Tuple) = {
    t.matchSeq {
      case Seq(graphName: Date) => {
        try{
          val startTime = new Date(graphName.getTime - node.duration)
          for (m <- meters)
            for(s <- cclient.getStatistics(m, startTime, graphName))
              using anchor t emit(graphName, m.name, s)
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
    }
    t.ack
  }
}