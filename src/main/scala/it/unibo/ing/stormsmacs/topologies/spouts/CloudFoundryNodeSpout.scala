package it.unibo.ing.stormsmacs.topologies.spouts

import java.io.IOException

import backtype.storm.utils.Utils
import storm.scala.dsl.{Logging, HttpRequesterSpout}
import uk.co.bigbeeconsultants.http.header.MediaType
import spray.json._
import spray.json.JsonParser.ParsingException
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.monit.model.JsonConversions._
import it.unibo.ing.stormsmacs.conf.CloudFoundryNode

/**
 * Created by tmnd on 18/11/14.
 */
class CloudFoundryNodeSpout(node : CloudFoundryNode, pollTime : Int)
    extends HttpRequesterSpout(List("monitData"),false, node.connectTimeout, node.readTimeout)
    with Logging
{
  override def nextTuple() = {
    Utils.sleep(pollTime)
    try{
      val response = httpClient.myGet(node.url, MediaType.APPLICATION_JSON)
      val body = response.body.asString
      logger.info(body)
      import spray.json.DefaultJsonProtocol._
      val data = body.parseJson.convertTo[Seq[MonitInfo]]
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