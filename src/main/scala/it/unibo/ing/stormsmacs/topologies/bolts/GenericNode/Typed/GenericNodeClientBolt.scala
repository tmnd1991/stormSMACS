package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.GenericNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.Typed.HttpRequesterBolt
import org.eclipse.jetty.client.ContentExchange
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
  override def typedExecute(t: Tuple1[Date], st : Tuple): Unit = {
    try{
      val exchange = new ContentExchange()
      exchange.setURI(node.url.toURI)
      exchange.setMethod("GET")
      httpClient.send(exchange)
      val state = exchange.waitForDone()
      val body = exchange.getResponseContent
      using anchor st emit (node, t._1, body.parseJson.convertTo[SigarMeteredData])
    }
    catch{
      case e : Throwable => {
        logger.error(e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }
}