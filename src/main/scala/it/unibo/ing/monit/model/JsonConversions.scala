package it.unibo.ing.monit.model

import java.util.concurrent.TimeUnit
import java.util.Date
import scala.concurrent.duration.Duration

import spray.json.DefaultJsonProtocol
import spray.json._

import it.unibo.ing.utils.DateUtils
/**
 * Created by tmnd on 03/11/14.
 */
object JsonConversions extends DefaultJsonProtocol {
  implicit object durationJsonFormat extends JsonFormat[Duration]{
    override def write(obj: Duration) = JsNumber(obj.toMillis)

    override def read(json: JsValue) = {
      json match {
        case jsnumber: JsNumber => Duration(jsnumber.value.toLong,TimeUnit.MILLISECONDS)
        case _ => throw new Exception("")
      }
    }
  }

  implicit val cpuUsageJsonFormat = jsonFormat3(CpuUsage)
  implicit val loadAverageJsonFormat = jsonFormat3(LoadAverage)

  implicit object monitInfoTypeJsonFormat extends JsonFormat[MonitInfoType] {
    override def write(obj: MonitInfoType) = JsString(obj.value)

    override def read(json: JsValue) = {
      json match {
        case jstring: JsString => MonitInfoType.values.getOrElse(jstring.value, throw new Exception(""))
        case _ => throw new Exception("")
      }
    }
  }

  implicit object monitMonitoringStatusJsonFormat extends JsonFormat[MonitMonitoringStatus] {
    override def write(obj: MonitMonitoringStatus) = JsString(obj.value)

    override def read(json: JsValue) = {
      json match {
        case jstring: JsString => MonitMonitoringStatus.values.getOrElse(jstring.value, throw new Exception(""))
        case _ => throw new Exception("")
      }
    }
  }


  implicit object monitStatusJsonParser extends JsonFormat[MonitStatus]{
    override def write(obj: MonitStatus) = JsString(obj.value)
    override def read(json: JsValue) = {
      json match {
        case jstring: JsString => MonitStatus.values.getOrElse(jstring.value, throw new Exception(""))
        case _ => throw new Exception("")
      }
    }
  }

  implicit object DateJsonFormat extends JsonFormat[Date] {
    def write(d: Date) =
      JsString(DateUtils.format(d))
    def read(value: JsValue) = value match {
      case JsString(date) =>
        DateUtils.parse(date)
      case _ => throw new Exception("")
    }
  }

  implicit val monitResponseTimeJsonParser = jsonFormat3(MonitResponseTime)

  implicit val monitProcessInfoJsonParser = jsonFormat16(MonitProcessInfo.apply)

  implicit val monitSystemInfoJsonParser = jsonFormat10(MonitSystemInfo.apply)


  implicit object MonitInfoJsonFormat extends JsonFormat[MonitInfo] {
    override def write(obj: MonitInfo) = obj match{
        case s : MonitSystemInfo => s.toJson
        case p : MonitProcessInfo => p.toJson
      }

    override def read(json: JsValue) = json match{
      case obj : JsObject =>{
        if (obj.fields.contains("pid"))
          obj.convertTo[MonitProcessInfo]
        else
          obj.convertTo[MonitSystemInfo]
      }
      case _ => throw new Exception("")
    }
  }

}
