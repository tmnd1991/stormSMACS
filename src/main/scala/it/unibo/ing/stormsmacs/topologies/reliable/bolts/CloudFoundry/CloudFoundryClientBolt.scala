package it.unibo.ing.stormsmacs.topologies.reliable.bolts.CloudFoundry

import java.net.URI

import backtype.storm.tuple.Tuple
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.monit.model.JsonConversions._
import it.unibo.ing.stormsmacs.conf.CloudFoundryNodeConf
import it.unibo.ing.stormsmacs.topologies.facilities.HttpRequesterBolt
import storm.scala.dsl.additions.Logging
import spray.json._
import java.util.Date
import it.unibo.ing.utils._
/**
 * @author Antonio Murgia
 * @version 24/12/14
 * Storm Bolt that gets Sample Data from given Cloudfoundry node
 */
class CloudFoundryClientBolt(node : CloudFoundryNodeConf, pollTime: Long)
  extends HttpRequesterBolt(node.connectTimeout, node.readTimeout, false,"Node","GraphName","MonitData")
  with Logging
{
  override def execute(t: Tuple) = t matchSeq{
    case Seq(date : Date) =>{
      try {
        logger.info("match")
        val url: URI = node.url.toURI / (date.getTime - pollTime).toString / date.getTime.toString
        val response = httpClient.doGET(uri = url, timeout = node.readTimeout)
        if (response isSuccess) {
          import spray.json.DefaultJsonProtocol._
          val data = response.content.parseJson.convertTo[Seq[MonitInfo]]
          for (d <- data){
            using anchor t emit(node, date, d)
          }
          t.ack
          logger.info("ack - got samples " + date)
        }
        else{
          logger.info("fail - not successfully http " + date.getTime)
        }
      }
      catch{
        case e : Throwable =>
          logger.error(e.getMessage, e)
          logger.info("fail - http error " + date)
      }
    }
  }
}
