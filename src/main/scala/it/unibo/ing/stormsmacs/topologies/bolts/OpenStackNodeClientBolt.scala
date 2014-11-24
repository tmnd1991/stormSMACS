package it.unibo.ing.stormsmacs.topologies.bolts

import java.io.IOException
import java.util.Date
import backtype.storm.tuple.Tuple
import it.unibo.ing.openstack.{TokenProvider, KeystoneTokenProvider}
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.OpenStackNode
import spray.json.DeserializationException
import spray.json.JsonParser.ParsingException
import storm.scala.dsl.Logging
import uk.co.bigbeeconsultants.http.header.MediaType

/**
 * Created by tmnd on 18/11/14.
 */

class OpenStackNodeClientBolt(node : OpenStackNode)
  extends HttpRequesterBolt(List("ceilometerData"), node.connectTimeout, node.readTimeout)
  with Logging{
  private var tokenProvider : TokenProvider = _
  setup{
    tokenProvider = KeystoneTokenProvider.getInstance(node.keystoneUrl, node.tenantName, node.username, node.password)
  }
  override def execute(t: Tuple) = t matchSeq{
    case Seq(graphName : Date) =>{
      try {
        val token = tokenProvider.token
        using anchor t emit token
      }
      catch {
        case e: IOException => {
          logger.warn(e.getMessage)
        }
        case e: ParsingException => {
          logger.error(e.getMessage)
        }
        case e: DeserializationException => {
          logger.error(e.getMessage)
        }
      }
    }
    t.ack
  }
}