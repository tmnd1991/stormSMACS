package it.unibo.ing.stormsmacs.rdfBindings

/**
 * @author Murgia Antonio
 * @version 15/12/14
 * Converters from Cloudfoundry data to Rdf
 */
import scala.language.postfixOps
import java.net.{URL}
import com.hp.hpl.jena.rdf.model.{Resource, Model, ModelFactory}
import com.hp.hpl.jena.vocabulary.RDF
import it.unibo.ing.monit.model.{MonitSystemInfo, MonitProcessInfo, MonitInfo}
import it.unibo.ing.rdf._
import it.unibo.ing.rdf.RdfWriter
import it.unibo.ing.utils._
import it.unibo.ing.utils.URLUtils._

case class CFNodeSample(url : URL, info : MonitInfo)
case class CFNodeResource(url : URL, info : MonitInfo)
object CFNodeDataRdfFormat{
  def nodeUrl(obj : CFNodeResource) : URL = URLUtils.removePort(obj.url) / obj.info.name
  def nodeUrl(obj : CFNodeSample) : URL = URLUtils.removePort(obj.url) / obj.info.name

  implicit object CFNodeResourceRDFWriter extends RdfWriter[CFNodeResource]{
    override def write(obj: CFNodeResource): Model = {
      val model = ModelFactory.createDefaultModel()
      model.setNsPrefixes(Properties.prefixes)
      obj info match {
        case m : MonitProcessInfo => write(model, nodeUrl(obj), m)
        case m : MonitSystemInfo  => write(model, nodeUrl(obj), m)
        case _ => model
      }
    }
    private def write(model : Model, url : URL, obj: MonitProcessInfo) : Model = {
      val statusURL = url / "status"
      val dataCollectedURL = url / "dataCollected"
      val childrenURL = url / "children"
      val monitoringStatusURL = url / "monitoringStatus"
      val parentPidURL = url / "parentPid"
      val pidURL = url / "pid"
      val uptimeURL = url / "uptime"
      val portResponseTimeURL = url / "portResponse" / "time"
      val portResponseDestinationURL = url / "portResponse" / "destination"
      val portResponseModeURL = url / "portResponse" / "mode"
      val cpuUsageURL = url / "cpu" / "usage"
      val memoryUsageURL = url / "memory" / "usage"
      val memoryUsagePercentageURL = url / "memory" / "usagePercentage"
      val unixSocketResponseTimeURL = url / "unixSocketResponse" / "time"
      val unixSocketResponseDestinationURL = url / "unixSocketResponse" / "destination"
      val unixSocketResponseModeURL = url / "unixSocketResponse" / "mode"

      model.createResource(statusURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(dataCollectedURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(childrenURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(monitoringStatusURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(parentPidURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(pidURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(uptimeURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "cumulative").
        addProperty(Properties.unit, "ms")

      if (obj.port_response_time.isDefined) {
        model.createResource(portResponseTimeURL).
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge").
          addProperty(Properties.unit, "ms")
        model.createResource(portResponseDestinationURL).
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge")
        model.createResource(portResponseModeURL).
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge")
      }

      model.createResource(cpuUsageURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(memoryUsageURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "kb")

      model.createResource(memoryUsagePercentageURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      if (obj.unix_socket_response_time.isDefined) {
        model.createResource(unixSocketResponseTimeURL).
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge").
          addProperty(Properties.unit, "ms")
        model.createResource(unixSocketResponseDestinationURL).
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge")
        model.createResource(unixSocketResponseModeURL).
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge")
      }
      return model
    }
    private def write(model : Model, url : URL, obj: MonitSystemInfo) : Model = {
      val statusURL = url / "status"
      val dataCollectedURL = url / "dataCollected"
      val monitoringStatusURL = url / "monitoringStatus"
      val cpuUsageUserURL = url / "cpu" / "usage" / "user"
      val cpuUsageSystemURL = url / "cpu" / "usage" / "system"
      val cpuUsageWaitURL = url / "cpu" / "usage" / "wait"
      val loadAverageURL = url / "load" / "average"
      val loadMinURL = url / "load" / "min"
      val loadMaxURL = url / "load" / "max"
      val memoryTotalUsageURL = url / "memory" / "totalUsage"
      val memoryPercentageUsageURL = url / "memory" / "percentageUsage"
      val swapTotalUsageURL = url / "swap" / "totalUsage"
      val swapPercentageUsageURL = url / "swap" / "percentageUsage"

      model.createResource(dataCollectedURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(cpuUsageUserURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(cpuUsageSystemURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(cpuUsageWaitURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(loadAverageURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(loadMinURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(loadMaxURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(memoryTotalUsageURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "kB")

      model.createResource(memoryPercentageUsageURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(statusURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(monitoringStatusURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(swapTotalUsageURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "kB")

      model.createResource(swapPercentageUsageURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      return model
    }
  }

  implicit object CFNodeSampleRDFWriter extends RdfWriter[CFNodeSample]{
    override def write(obj: CFNodeSample): Model = {
      val model = ModelFactory.createDefaultModel()
      model.setNsPrefixes(Properties.prefixes)
      obj info match {
        case m : MonitProcessInfo => write(model, nodeUrl(obj), m)
        case m : MonitSystemInfo  => write(model, nodeUrl(obj), m)
        case _ => model
      }
    }
    private def write(model : Model, url : String, obj: MonitProcessInfo) : Model = {
      val statusURL = url / "status"
      val dataCollectedURL = url / "dataCollected"
      val childrenURL = url / "children"
      val monitoringStatusURL = url / "monitoringStatus"
      val parentPidURL = url / "parentPid"
      val pidURL = url / "pid"
      val uptimeURL = url / "uptime"
      val portResponseTimeURL = url / "portResponse" / "time"
      val portResponseDestinationURL = url / "portResponse" / "destination"
      val portResponseModeURL = url / "portResponse" / "mode"
      val cpuUsageURL = url / "cpu" / "usage"
      val memoryUsageURL = url / "memory" / "usage"
      val memoryUsagePercentageURL = url / "memory" / "usagePercentage"
      val unixSocketResponseTimeURL = url / "unixSocketResponse" / "time"
      val unixSocketResponseDestinationURL = url / "unixSocketResponse" / "destination"
      val unixSocketResponseModeURL = url / "unixSocketResponse" / "mode"

      model.createResource(statusURL).
        addProperty(RDF.value, obj.status.toString)

      model.createResource(dataCollectedURL).
        addProperty(RDF.value, DateUtils.format(obj.data_collected))

      model.createResource(childrenURL).
        addProperty(RDF.value, "" + obj.children)

      model.createResource(monitoringStatusURL).
        addProperty(RDF.value, obj.monitoring_status.toString)

      model.createResource(parentPidURL).
        addProperty(RDF.value, "" + obj.parent_pid)

      model.createResource(pidURL).
        addProperty(RDF.value, "" + obj.pid)

      model.createResource(uptimeURL).
        addProperty(RDF.value, "" + obj.uptime.toMillis)

      if (obj.port_response_time.isDefined) {
        model.createResource(portResponseTimeURL).
          addProperty(RDF.value, "" + obj.port_response_time.get.d.toMillis)
        model.createResource(portResponseDestinationURL).
          addProperty(RDF.value, obj.port_response_time.get.url)
        model.createResource(portResponseModeURL).
          addProperty(RDF.value, obj.port_response_time.get.mode)
      }

      model.createResource(cpuUsageURL).
        addProperty(RDF.value, "" + obj.cpu_percent)

      model.createResource(memoryUsageURL).
        addProperty(RDF.value, "" + obj.memory_kb)

      model.createResource(memoryUsagePercentageURL).
        addProperty(RDF.value, "" + obj.memory_perc)

      if (obj.unix_socket_response_time.isDefined) {
        model.createResource(unixSocketResponseTimeURL).
          addProperty(RDF.value, "" + obj.unix_socket_response_time.get.d.toMillis)
        model.createResource(unixSocketResponseDestinationURL).
          addProperty(RDF.value, obj.unix_socket_response_time.get.url)
        model.createResource(unixSocketResponseModeURL).
          addProperty(RDF.value, obj.unix_socket_response_time.get.mode)
      }
      return model
    }
    private def write(model : Model, url : String, obj: MonitSystemInfo) : Model = {

      val statusURL = url / "status"
      val dataCollectedURL = url / "dataCollected"
      val monitoringStatusURL = url / "monitoringStatus"
      val cpuUsageUserURL = url / "cpu" / "usage" / "user"
      val cpuUsageSystemURL = url / "cpu" / "usage" / "system"
      val cpuUsageWaitURL = url / "cpu" / "usage" / "wait"
      val loadAverageURL = url / "load" / "average"
      val loadMinURL = url / "load" / "min"
      val loadMaxURL = url / "load" / "max"
      val memoryTotalUsageURL = url / "memory" / "totalUsage"
      val memoryPercentageUsageURL = url / "memory" / "percentageUsage"
      val swapTotalUsageURL = url / "swap" / "totalUsage"
      val swapPercentageUsageURL = url / "swap" / "percentageUsage"


      model.createResource(dataCollectedURL).
        addProperty(RDF.value, DateUtils.format(obj.data_collected))

      model.createResource(cpuUsageUserURL).
        addProperty(RDF.value, "" + obj.cpu.user)

      model.createResource(cpuUsageSystemURL).
        addProperty(RDF.value, "" + obj.cpu.system)

      model.createResource(cpuUsageWaitURL).
        addProperty(RDF.value, "" + obj.cpu.Wait)

      model.createResource(loadAverageURL).
        addProperty(RDF.value, "" + obj.load_average.avg)

      model.createResource(loadMinURL).
        addProperty(RDF.value, "" + obj.load_average.min)

      model.createResource(loadMaxURL).
        addProperty(RDF.value, "" + obj.load_average.max)

      model.createResource(memoryTotalUsageURL).
        addProperty(RDF.value, "" + obj.memory_usage)

      model.createResource(memoryPercentageUsageURL).
        addProperty(RDF.value, "" + obj.memory_usage_perc)

      model.createResource(statusURL).
        addProperty(RDF.value, obj.status.toString)

      model.createResource(monitoringStatusURL).
        addProperty(RDF.value, obj.monitoring_status.toString)

      model.createResource(swapTotalUsageURL).
        addProperty(RDF.`type`, "" + obj.swap_usage)

      model.createResource(swapPercentageUsageURL).
        addProperty(RDF.`type`, "" + obj.swap_usage_perc)

      return model
    }
  }
}