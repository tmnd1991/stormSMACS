package it.unibo.ing.stormsmacs.conf

/**
 * Created by tmnd on 18/11/14.
 */


/**
 * @author Antonio Murgia
 * @constructor represents the configuration of a stormsmacs instance.
 * @param openstackNodes     Optional non-empty list of OpenStackNode monitored through the ceilometer endpoint
 * @param cloudfoundryNodes  Optional non-empty list of CloudFoundryNode monitored through the restful endpoint written on top of monit daemon
 * @param genericNodes       Optional non-empty list of GenericNode monitored through the restful endpoint written on top of java cigar API
 * @param persisterNode      persister endpoint used as TDB
 * @param debug              storm debug mode
 * @param pollTime           poll time of monitoring
 */
case class JsonConfiguration(name              : String,
                             openstackNodes    : Option[Seq[OpenStackNodeConf]],
                             cloudfoundryNodes : Option[Seq[CloudFoundryNodeConf]],
                             genericNodes      : Option[Seq[GenericNodeConf]],
                             persisterNode     : PersisterNodeConf,
                             debug             : Boolean,
                             remote            : Boolean,
                             pollTime          : Long) extends Configuration{
  require(openstackNodes == None || openstackNodes.get.nonEmpty)
  require(cloudfoundryNodes == None || cloudfoundryNodes.get.nonEmpty)
  require(genericNodes == None || genericNodes.get.nonEmpty)
  require(persisterNode != null)
  require(pollTime > 0)
}
object JsonConfiguration{
  import spray.json._
  import spray.json.MyMod._
  import it.unibo.ing.stormsmacs.conf.JsonConfigurationProtocol._
  def readJsonConf(s : String) : Configuration = s.parseJson.convertTo[JsonConfiguration]
  def tryReadJsonConf(s : String) : Option[Configuration] = {
    s.tryParseJson match{
      case Some(v : JsValue) => v.tryConvertTo[JsonConfiguration]
      case _                 => None
    }
  }
}
object JsonConfigurationProtocol extends spray.json.DefaultJsonProtocol{
  import spray.json._
  import DefaultJsonProtocol._
  import java.net.URL
  import OpenStackNodeProtocol._
  import CloudFoundryNodeProtocol._
  import GenericNodeProtocol._
  import PersisterNodeProtocol._
  implicit val jsonConfigurationFormat : JsonFormat[JsonConfiguration] = jsonFormat8(JsonConfiguration.apply)
  implicit object urlFormat extends JsonFormat[URL]{
    override def read(json: JsValue) = json match{
      case s : JsString => try{
        new URL(s.value)
      }
      catch{
        case _ : Throwable => throw new Exception()
      }
      case _ => throw new Exception()
    }
    override def write(obj: URL) = JsString(obj.toString)
  }
}

