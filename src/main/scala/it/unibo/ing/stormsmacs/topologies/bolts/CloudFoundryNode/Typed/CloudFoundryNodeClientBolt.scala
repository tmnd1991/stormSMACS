package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import java.net.URI

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
class CloudFoundryNodeClientBolt(node : CloudFoundryNodeConf, pollTime: Long)
  extends HttpRequesterBolt(node.connectTimeout, node.readTimeout, false,"Node","GraphName","MonitData")
  with Logging
{
  override def execute(t: Tuple) = t matchSeq{
    case Seq(date : Date) =>{
      try {
        val url: URI = node.url.toURI / (date.getTime - pollTime).toString / date.getTime.toString
        val response = httpClient.doGET(uri = url, timeout = node.readTimeout)
        if (response isSuccess) {
          import spray.json.DefaultJsonProtocol._
          val data = response.content.parseJson.convertTo[Seq[MonitInfo]]
          var i = 0
          for (d <- data){
            logger.info(s"emitting $i of ${data.size} of: $date")
            using anchor t emit(node, date, d)
          }
          logger.info("ack - got samples " + date)
          t ack
        }
        else{
          logger.info("fail - not successfully http " + date)
          t fail
        }

      }
      catch{
        case e : Throwable =>
          logger.error(e.getMessage, e)
          logger.info("fail - no samples " + date)
          t fail
      }

    }
  }
}
