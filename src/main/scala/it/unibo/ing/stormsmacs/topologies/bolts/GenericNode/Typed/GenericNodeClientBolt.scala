package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import java.util.Date

import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.GenericNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.Typed.HttpRequesterBolt
import storm.scala.dsl.Logging
import spray.json._
import it.unibo.ing.sigar.restful.model.SigarMeteredDataFormat._
/**
 * @author Antonio Murgia
 * @version 18/11/14
 */
class GenericNodeClientBolt(val node : GenericNodeConf)
  extends HttpRequesterBolt[Tuple1[Date], (GenericNodeConf, Date, SigarMeteredData)](node.connectTimeout, node.readTimeout, false, "Node","GraphName","MonitData")
  with Logging
{
  override def typedExecute(t: Tuple1[Date]): Seq[(GenericNodeConf, Date, SigarMeteredData)] = {
    val response = httpClient.GET(node.url.toURI)
    val body = response.getContentAsString
    Seq((node, t._1, body.parseJson.convertTo[SigarMeteredData]))
  }
}