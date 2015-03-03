package it.unibo.ing.stormsmacs.conf

import java.net.URL

import it.unibo.ing.stormsmacs.conf.PersisterNodeType.VirtuosoNodeType

/**
 * Created by Antonio on 02/03/2015.
 */
case class VirtuosoNodeConf( id : String,
                             url : String,
                             username : String,
                             password : String,
                             `type` : PersisterNodeType = VirtuosoNodeType) extends PersisterNodeConf{
}

object VirtuosoNodeProtocol extends spray.json.DefaultJsonProtocol{
  import spray.json._
  import it.unibo.ing.stormsmacs.conf.JsonConfigurationProtocol._
  import it.unibo.ing.stormsmacs.conf.PersisterNodeProtocol._
  implicit val viruosoNodeFormat = jsonFormat5(VirtuosoNodeConf)
}
