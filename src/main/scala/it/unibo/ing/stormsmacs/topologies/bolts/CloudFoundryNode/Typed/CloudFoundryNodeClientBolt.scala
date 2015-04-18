package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import backtype.storm.tuple.Tuple
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.monit.model.JsonConversions._
import it.unibo.ing.stormsmacs.conf.CloudFoundryNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.Typed.HttpRequesterBolt
import storm.scala.dsl.additions.Logging
import spray.json._
import java.util.Date
import it.unibo.ing.utils._
/**
 * @author Antonio Murgia
 * @version 24/12/14
 * Storm Bolt that gets Sample Data from given Cloudfoundry node
 */
class CloudFoundryNodeClientBolt(node : CloudFoundryNodeConf)
  extends HttpRequesterBolt(node.connectTimeout, node.readTimeout, false,"Node","GraphName","MonitData")
  with Logging
{
  override def execute(t: Tuple) = t matchSeq{
    case Seq(date : Date) =>{
      try {
        val response = httpClient.doGET(uri = node.url.toURI / date.getTime.toString, timeout = node.readTimeout)
        if (response isSuccess) {
          import spray.json.DefaultJsonProtocol._
          val data = response.content.parseJson.convertTo[Seq[MonitInfo]]
          for (d <- data)
            using anchor t emit(node, date, d)
        }
      }
      catch{
        case r : RuntimeException => logger.error(r.getMessage, r)
        case e : Throwable => logger.error(e.getMessage, e)
      }
      finally{
        t ack
      }
    }
  }
}
