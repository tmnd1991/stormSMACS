package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.monit.model.JsonConversions._
import it.unibo.ing.stormsmacs.conf.CloudFoundryNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.Typed.HttpRequesterBolt
import storm.scala.dsl.{Logging}
import spray.json._
import java.util.Date
/**
 * Created by tmnd91 on 24/12/14.
 */
class CloudFoundryNodeClientBolt(node : CloudFoundryNodeConf)
  extends HttpRequesterBolt[Tuple1[Date],(CloudFoundryNodeConf, Date, MonitInfo)](node.connectTimeout, node.readTimeout, false,"Node","GraphName","MonitData")
  with Logging
{
  override def typedExecute(t: Tuple1[Date]): Seq[(CloudFoundryNodeConf, Date, MonitInfo)] = {
    val response = httpClient.GET(node.url.toURI)
    val body = response.getContentAsString
    logger.info(body)
    import spray.json.DefaultJsonProtocol._
    val data = body.parseJson.convertTo[Seq[MonitInfo]]
    data.map((node, t._1, _))
  }
}
