package it.unibo.ing.stormsmacs.conf

import java.net.URL

/**
 * Created by Antonio on 02/03/2015.
 */
case class VirtuosoNodeConf( id : String,
                             `type` : PersisterNodeType,
                             url : String,
                             username : String,
                             password : String) extends PersisterNodeConf(PersisterNodeType.VirtuosoNodeType){
}

object VirtuosoNodeProtocol extends spray.json.DefaultJsonProtocol{
  import spray.json._
  import it.unibo.ing.stormsmacs.conf.JsonConfigurationProtocol._
  import it.unibo.ing.stormsmacs.conf.PersisterNodeProtocol._
  implicit val viruosoNodeFormat = jsonFormat5(VirtuosoNodeConf)
}
