package it.unibo.ing.stormsmacs.topologies.bolts

import java.io.IOException
import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.monit.model.JsonConversions._
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.stormsmacs.conf.CloudFoundryNode
import spray.json.JsonParser.ParsingException
import spray.json._
import storm.scala.dsl.Logging


/**
 * @author Antonio Murgia
 * @version 18/11/14
 */
class CloudFoundryNodeClientBolt(val node : CloudFoundryNode)
    extends ClientBolt(List("NodeName","GraphName","MonitData"), node.connectTimeout, node.readTimeout)
    with Logging
{
  override def emitData(t : Tuple, graphName : Date) = {
      val response = httpClient.GET(node.url.toURI)
      val body = response.getContentAsString
      logger.info(body)
      import spray.json.DefaultJsonProtocol._
      val data = body.parseJson.convertTo[Seq[MonitInfo]]
      using anchor t emit (node.id, graphName, data)
  }
}