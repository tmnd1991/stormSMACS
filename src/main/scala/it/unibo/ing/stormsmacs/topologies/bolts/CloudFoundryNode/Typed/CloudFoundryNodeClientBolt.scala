package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import backtype.storm.tuple.Tuple
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.monit.model.JsonConversions._
import it.unibo.ing.stormsmacs.conf.CloudFoundryNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.Typed.HttpRequesterBolt
import storm.scala.dsl.{StormTuple, Logging}
import spray.json._
import java.util.Date
/**
 * @author Antonio Murgia
 * @version 24/12/14
 */
class CloudFoundryNodeClientBolt(node : CloudFoundryNodeConf)
  extends HttpRequesterBolt[Tuple1[Date],(CloudFoundryNodeConf, Date, MonitInfo)](node.connectTimeout, node.readTimeout, false,"Node","GraphName","MonitData")
  with Logging
{
  override def typedExecute(t: Tuple1[Date], st : Tuple) {
    try{
      val response = httpClient.GET(node.url.toURI)
      val body = response.getContentAsString
      import spray.json.DefaultJsonProtocol._
      val data = body.parseJson.convertTo[Seq[MonitInfo]]
      for (d <- data)
        using anchor st emit(node, t._1, d)
    }
    catch{
      case e : Throwable => {
        logger.error(e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }
}
