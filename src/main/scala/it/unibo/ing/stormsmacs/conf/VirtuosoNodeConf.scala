package it.unibo.ing.stormsmacs.conf

import java.net.URL

/**
 * Created by Antonio on 02/03/2015.
 */
case class VirtuosoNodeConf( id : String,
                             url : String,
                             username : String,
                             password : String) extends PersisterNodeConf{
}

object VirtuosoNodeProtocol extends spray.json.DefaultJsonProtocol{
  import spray.json._
  import it.unibo.ing.stormsmacs.conf.JsonConfigurationProtocol._
  implicit val viruosoNodeFormat = jsonFormat4(VirtuosoNodeConf)
}
