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
import uk.co.bigbeeconsultants.http.header.MediaType

/**
 * Created by tmnd on 18/11/14.
 */
class CloudFoundryNodeClientBolt(node : CloudFoundryNode)
    extends ClientBolt(List("MonitData"), node.connectTimeout, node.readTimeout)
    with Logging
{
  override def emitData(t : Tuple, graphName : Date) = {
    val response = httpClient.myGet(node.url, MediaType.APPLICATION_JSON)
    val body = response.body.asString
    logger.info(body)
    import spray.json.DefaultJsonProtocol._
    val data = body.parseJson.convertTo[Seq[MonitInfo]]
    using anchor t emit (graphName, data)
  }
}