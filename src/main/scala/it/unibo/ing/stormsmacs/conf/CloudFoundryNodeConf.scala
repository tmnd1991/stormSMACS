package it.unibo.ing.stormsmacs.conf

import java.net.URL
/**
 * @author Antonio Murgia
 * @constructor Representation of a CloudFoundryNode
 * @param id    id of the node (arbitrary, just an internal representantion, should be unique)
 * @param connect_timeout connect timeout in ms default is 2000ms
 * @param read_timeout  read timeout in ms default is 1000ms
 * Representation of a Cloudfoundry Node
 */

case class CloudFoundryNodeConf (id : String,
                        url : URL,
                        private val connect_timeout : Option[Int],
                        private val read_timeout : Option[Int]){
  def connectTimeout = connect_timeout.getOrElse(2000)
  def readTimeout = read_timeout.getOrElse(1000)
  override def toString = "CloudFoundryNode[ " + id + " @ " + url.toString + "\n" +
                                            "c timeout -> " + connectTimeout + "\n" +
                                            "r timeout -> " + readTimeout + "]"
}
object CloudFoundryNodeProtocol extends spray.json.DefaultJsonProtocol{
  import spray.json._
  import it.unibo.ing.stormsmacs.conf.JsonConfigurationProtocol._
  implicit val cloudfoundryNodeFormat = jsonFormat4(CloudFoundryNodeConf)
}
