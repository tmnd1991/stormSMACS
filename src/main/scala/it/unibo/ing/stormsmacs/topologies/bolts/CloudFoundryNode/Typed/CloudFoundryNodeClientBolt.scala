package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import backtype.storm.tuple.Tuple
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.monit.model.JsonConversions._
import it.unibo.ing.stormsmacs.conf.CloudFoundryNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.Typed.HttpRequesterBolt
import storm.scala.dsl.additions.Logging
import spray.json._
import java.util.Date
/**
 * @author Antonio Murgia
 * @version 24/12/14
 * Storm Bolt that gets Sample Data from given Cloudfoundry node
 */
class CloudFoundryNodeClientBolt(node : CloudFoundryNodeConf)
  extends HttpRequesterBolt(node.connectTimeout, node.readTimeout, false,"Node","GraphName","MonitData")
  with Logging
{

  override def execute(t: Tuple) = {
    try{
      t matchSeq{
        case Seq(date : Date) =>{
          val response = httpClient.doGET(uri = node.url.toURI, timeout = node.readTimeout)
          if (response isSuccess){
            import spray.json.DefaultJsonProtocol._
            val data = response.content.parseJson.convertTo[Seq[MonitInfo]]
            for (d <- data)
              using anchor t emit (node, date, d)
            t ack
          }
          else{
            t fail
          }
        }
        case x => logger.error("invalid input tuple: expected Date and " + x + "found")
      }
    }
    catch{
      case e : Throwable => {
        logger.trace("", e)
        t ack
      }
    }
  }
}
