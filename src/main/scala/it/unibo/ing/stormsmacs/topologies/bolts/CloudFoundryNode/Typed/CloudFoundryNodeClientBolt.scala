package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import backtype.storm.tuple.Tuple
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.monit.model.JsonConversions._
import it.unibo.ing.stormsmacs.conf.CloudFoundryNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.Typed.HttpRequesterBolt
import org.eclipse.jetty.client.ContentExchange
import storm.scala.dsl.{StormTuple, Logging}
import spray.json._
import java.util.Date
/**
 * @author Antonio Murgia
 * @version 24/12/14
 * Storm Bolt that gets Sample Data from given Cloudfoundry node
 */
class CloudFoundryNodeClientBolt(node : CloudFoundryNodeConf)
  extends HttpRequesterBolt[Tuple1[Date],(CloudFoundryNodeConf, Date, MonitInfo)](node.connectTimeout, node.readTimeout, false,"Node","GraphName","MonitData")
  with Logging
{
  override def typedExecute(t: Tuple1[Date], st : Tuple) {
    try{
      val exchange = new ContentExchange()
      exchange.setURI(node.url.toURI)
      exchange.setMethod("GET")
      exchange.setTimeout(node.readTimeout)
      httpClient.send(exchange)
      val state = exchange.waitForDone()
      val body = exchange.getResponseContent
      import spray.json.DefaultJsonProtocol._
      val data = body.parseJson.convertTo[Seq[MonitInfo]]
      for (d <- data)
        using anchor st emit(node, t._1, d)
      st.ack
    }
    catch{
      case e : Throwable => {
        logger.error(e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }
}
