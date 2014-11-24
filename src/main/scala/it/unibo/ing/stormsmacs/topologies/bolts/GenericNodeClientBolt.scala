package it.unibo.ing.stormsmacs.topologies.bolts

import java.io.IOException
import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.sigar.restful.model.SigarMeteredDataFormat._
import it.unibo.ing.stormsmacs.conf.GenericNode
import spray.json.JsonParser.ParsingException
import spray.json._
import storm.scala.dsl.{HttpRequesterSpout, Logging}
import uk.co.bigbeeconsultants.http.header.MediaType

/**
 * Created by tmnd on 18/11/14.
 */
class GenericNodeClientBolt(node : GenericNode) extends HttpRequesterBolt(List("sigarData"), node.connectTimeout, node.readTimeout)
with Logging
{
  override def execute(t : Tuple) = t matchSeq{
    case Seq(graphName: Date) => {
      try {
        val response = httpClient.myGet(node.url, MediaType.APPLICATION_JSON)
        val body = response.body.asString
        logger.info(body)
        val data = body.parseJson.convertTo[SigarMeteredData]
        t.emit(graphName, data)
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