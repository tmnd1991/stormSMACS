package it.unibo.ing.stormsmacs.topologies.spouts

import java.io.IOException

import backtype.storm.utils.Utils
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.sigar.restful.model.SigarMeteredDataFormat._
import it.unibo.ing.stormsmacs.conf.GenericNode
import spray.json._
import spray.json.JsonParser.ParsingException
import storm.scala.dsl.{Logging, HttpRequesterSpout}
import uk.co.bigbeeconsultants.http.header.MediaType

/**
 * Created by tmnd on 18/11/14.
 */
class GenericNodeSpout(node : GenericNode, pollTime : Int) extends HttpRequesterSpout(List("monitData"),false, node.connectTimeout, node.readTimeout)
with Logging
{
  override def nextTuple() = {
    Utils.sleep(pollTime)
    try{
      val response = httpClient.myGet(node.url, MediaType.APPLICATION_JSON)
      val body = response.body.asString
      logger.info(body)
      val data = body.parseJson.convertTo[SigarMeteredData]
      emit(data)
    }
    catch{
      case e : IOException => {
        logger.warn(e.getMessage)
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