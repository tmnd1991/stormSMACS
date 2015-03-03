package it.unibo.ing.stormsmacs.conf

import java.net.URL
/**
 * @author Antonio Murgia
 * @constructor Representation of a FusekiNode the TDB endpoint
 * @param id    id of the node (arbitrary, just an internal representation, should be unique)
 * @param url   fuseki endpoint
 */

case class FusekiNodeConf (id : String,
                           `type` : PersisterNodeType,
                           url : String) extends PersisterNodeConf(id, url, `type`){
  override def toString = "FusekiNode[ " + id + " @ " + url.toString + " ]"
}
object FusekiNodeProtocol extends spray.json.DefaultJsonProtocol{
  import spray.json._
  import it.unibo.ing.stormsmacs.conf.PersisterNodeProtocol._

  implicit val fusekiNodeFormat = jsonFormat3(FusekiNodeConf.apply)
}
