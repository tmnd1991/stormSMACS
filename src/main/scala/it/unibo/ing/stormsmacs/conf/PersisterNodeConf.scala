package it.unibo.ing.stormsmacs.conf

import org.openstack.api.restful.MalformedJsonException

/**
 * Created by Antonio on 02/03/2015.
 */
abstract class PersisterNodeConf {
  val id : String
  val url : String
}
object PeristerNodeProtocol extends spray.json.DefaultJsonProtocol{
  import spray.json._
  import it.unibo.ing.stormsmacs.conf.JsonConfigurationProtocol._
  implicit object PersisterNodeFormat extends RootJsonFormat[PersisterNodeConf]{
    import FusekiNodeProtocol._
    import VirtuosoNodeProtocol._
    override def write(obj: PersisterNodeConf): JsValue = {
      obj match{
        case x : FusekiNodeConf => x.toJson
        case x : VirtuosoNodeConf => x.toJson
      }
    }
    override def read(json: JsValue): PersisterNodeConf = {
      json match{
        case obj : JsObject =>{
          if (obj.fields.contains("username")) json.convertTo[VirtuosoNodeConf]
          else                                 json.convertTo[FusekiNodeConf]
        }
        case _ => throw new MalformedJsonException()
      }
    }
  }
}

