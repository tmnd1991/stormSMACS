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
class GenericNodeClientBolt(node : GenericNode)
  extends ClientBolt(List("MonitData"), node.connectTimeout, node.readTimeout)
  with Logging
{
  override def emitData(t: Tuple, graphName: Date) = {
    val response = httpClient.myGet(node.url, MediaType.APPLICATION_JSON)
    val body = response.body.asString
    logger.info(body)
    val data = body.parseJson.convertTo[SigarMeteredData]
    using anchor t emit(graphName, data)
  }
}