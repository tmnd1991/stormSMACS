package it.unibo.ing.stormsmacs.topologies.bolts

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.sigar.restful.model.SigarMeteredDataFormat._
import it.unibo.ing.stormsmacs.conf.GenericNodeConf

import spray.json._
import storm.scala.dsl.Logging

/**
 * @author Antonio Murgia
 * @version 18/11/14
 */
class GenericNodeClientBolt(val node : GenericNodeConf)
  extends ClientBolt(List("Node","GraphName","MonitData"), node.connectTimeout, node.readTimeout)
  with Logging
{
  override def emitData(t: Tuple, graphName: Date) = {
      val response = httpClient.GET(node.url.toURI)
      val body = response.getContentAsString
      logger.info(body)
      val data = body.parseJson.convertTo[SigarMeteredData]
      using anchor t emit(node, graphName, data)
  }
}