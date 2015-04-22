package it.unibo.ing.stormsmacs.conf

import java.net.URL

/**
 * @author Antonio Murgia
 * @constructor Representation of a OpenstackNode
 * @param id    id of the node (arbitrary, just an internal representantion, should be unique)
 * @param tenantName  Openstack tenantName
 * @param ceilometerUrl  valid url address of the ceilometer endpoint
 * @param keystoneUrl url of the keystone endpoint
 * @param username username to log into keystone and get a valid token
 * @param password password to log into keystone and get a valid token
 * @param connect_timeout connect timeout in ms default is 2000ms
 * @param read_timeout  read timeout in ms default is 1000ms
 * Representation of an Openstack Node
 */
case class OpenStackNodeConf(id : String,
                        tenantName: String,
                        ceilometerUrl	: URL,
                        keystoneUrl : URL,
                        username : String,
                        password : String,
                        connect_timeout : Int,
                        read_timeout : Int){
  def connectTimeout = connect_timeout
  def readTimeout = read_timeout
  override def toString = "OpenstackNode[ " + id + " @ " + ceilometerUrl.toString + "\n" +
    "c timeout -> " + connectTimeout + "\n" +
    "r timeout -> " + readTimeout + "]"
}

object OpenStackNodeProtocol extends spray.json.DefaultJsonProtocol{
  import spray.json._
  import it.unibo.ing.stormsmacs.conf.JsonConfigurationProtocol._
  implicit val openstackNodeFormat = jsonFormat8(OpenStackNodeConf)
}
