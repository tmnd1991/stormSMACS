package it.unibo.ing.openstack

import java.net.URL

/**
 * Created by tmnd on 10/11/14.
 */
abstract class TokenProvider(val host : URL, val tenantName : String,  val username : String, val password : String) extends Serializable{
  def token : String
  def tokenOption : Option[String] = {
    try{
      Some(token)
    }
    catch{
      case _ : Throwable => None
    }
  }
}
