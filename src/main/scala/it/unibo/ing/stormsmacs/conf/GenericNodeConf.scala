package it.unibo.ing.stormsmacs.conf

import java.net.URL
/**
 * @author Antonio Murgia
 * @constructor Representation of a GenericNode monitored through restful wrapper around cigar API
 * @param id    id of the node (arbitrary, just an internal rappresentantion, should be unique)
 * @param connect_timeout connect timeout in ms default is 2000ms
 * @param read_timeout  read timeout in ms default is 1000ms
 */

case class GenericNodeConf (id : String,
                       url : URL,
                       private val connect_timeout : Option[Int],
                       private val read_timeout : Option[Int]){
  def connectTimeout = connect_timeout.getOrElse(2000)
  def readTimeout = read_timeout.getOrElse(1000)

  override def toString = "GenericNode[ " + id + " @ " + url.toString + "\n" +
                                      "c timeout -> " + connectTimeout + "\n" +
                                      "r timeout -> " + readTimeout + "]"
}
object GenericNodeProtocol extends spray.json.DefaultJsonProtocol{
  import spray.json._
  import it.unibo.ing.stormsmacs.conf.JsonConfigurationProtocol._
  implicit val genericNodeFormat = jsonFormat4(GenericNodeConf)
}

