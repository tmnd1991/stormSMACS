package it.unibo.ing.stormsmacs.rdfBindings

/**
 * @author Murgia Antonio
 * @version 15/12/14
 */
import scala.language.postfixOps
import java.net.{URI, URL}
import com.hp.hpl.jena.graph.impl.SimpleGraphMaker
import com.hp.hpl.jena.rdf.model.{Resource, Model, ModelFactory}
import com.hp.hpl.jena.vocabulary.RDF
import it.unibo.ing.monit.model.{MonitSystemInfo, MonitProcessInfo, MonitInfo}
import it.unibo.ing.rdf._
import it.unibo.ing.rdf.RdfWriter
import it.unibo.ing.utils._

case class CFNodeSample(url : URL, info : MonitInfo)
case class CFNodeResource(url : URL, info : MonitInfo)
object CFNodeDataRdfFormat{
  import scala.collection.JavaConversions._
  implicit object CFNodeResourceRDFWriter extends RdfWriter[CFNodeResource]{
    override def write(obj: CFNodeResource): Model = {
      val model = ModelFactory.createDefaultModel()
      model.setNsPrefixes(Properties.prefixes)
      val nodeUrl = (obj.url / "CF").toString
      obj info match {
        case m : MonitProcessInfo => write(model, nodeUrl, m)
        case m : MonitSystemInfo  => write(model, nodeUrl, m)
        case _ => model
      }
    }
    private def write(model : Model, url : String, obj: MonitProcessInfo) : Model = {
      model.createResource(url / "status").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(url / "dataCollected").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(url / "children").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(url / "monitoringStatus").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(url / "parentPid").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(url / "pid").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(url / "uptime").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "cumulative").
        addProperty(Properties.unit, "ms")

      if (obj.port_response_time.isDefined) {
        model.createResource(url / "portResponse" / "time").
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge").
          addProperty(Properties.unit, "ms")
        model.createResource(url / "portResponse" / "destination").
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge")
        model.createResource(url / "portResponse" / "mode").
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge")
      }

      model.createResource(url / "cpu" / "usage").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(url / "memory" / "usage").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "kb")

      model.createResource(url / "memory" / "usagePercentage").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      if (obj.unix_socket_response_time.isDefined) {
        model.createResource(url / "unixSocketResponse" / "time").
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge").
          addProperty(Properties.unit, "ms")
        model.createResource(url / "unixSocketResponse" / "destination").
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge")
        model.createResource(url / "unixSocketResponse" / "mode").
          addProperty(RDF.`type`, "Resource").
          addProperty(Properties.sampleType, "gauge")
      }
      return model
    }
    private def write(model : Model, url : String, obj: MonitSystemInfo) : Model = {
      model.createResource(url / "dataCollected").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(url / "cpuUsage" / "user").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(url / "cpuUsage" / "system").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(url / "cpuUsage" / "wait").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(url / "load" / "average").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(url / "load" / "min").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(url / "load" / "max").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(url / "memory" / "totalUsage").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "kB")

      model.createResource(url / "memory" / "percentageUsage").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      model.createResource(url / "status").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(url / "monitoringStatus").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      model.createResource(url / "swap" / "totalUsage").
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "kB")

      model.createResource(url / "swap" / "percentageUsage").
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
      val nodeUrl = (obj.url / "CF").toString
      obj info match {
        case m : MonitProcessInfo => write(model, nodeUrl, m)
        case m : MonitSystemInfo  => write(model, nodeUrl, m)
        case _ => model
      }
    }
    private def write(model : Model, url : String, obj: MonitProcessInfo) : Model = {
      model.createResource(url / "status").
        addProperty(RDF.value, obj.status.toString)

      model.createResource(url / "dataCollected").
        addProperty(RDF.value, DateUtils.format(obj.data_collected))

      model.createResource(url / "children").
        addProperty(RDF.value, "" + obj.children)

      model.createResource(url / "monitoringStatus").
        addProperty(RDF.value, obj.monitoring_status.toString)

      model.createResource(url / "parentPid").
        addProperty(RDF.value, "" + obj.parent_pid)

      model.createResource(url / "pid").
        addProperty(RDF.value, "" + obj.pid)

      model.createResource(url / "uptime").
        addProperty(RDF.value, "" + obj.uptime.toMillis)

      if (obj.port_response_time.isDefined) {
        model.createResource(url / "portResponse" / "time").
          addProperty(RDF.value, "" + obj.port_response_time.get.d.toMillis)
        model.createResource(url / "portResponse" / "destination").
          addProperty(RDF.value, obj.port_response_time.get.url)
        model.createResource(url / "portResponse" / "mode").
          addProperty(RDF.value, obj.port_response_time.get.mode)
      }

      model.createResource(url / "cpu" / "usage").
        addProperty(RDF.value, "" + obj.cpu_percent)

      model.createResource(url / "memory" / "usage").
        addProperty(RDF.value, "" + obj.memory_kb)

      model.createResource(url / "memory" / "usagePercentage").
        addProperty(RDF.value, "" + obj.memory_perc)

      if (obj.unix_socket_response_time.isDefined) {
        model.createResource(url / "unixSocketResponse" / "time").
          addProperty(RDF.value, "" + obj.unix_socket_response_time.get.d.toMillis)
        model.createResource(url / "unixSocketResponse" / "destination").
          addProperty(RDF.value, obj.unix_socket_response_time.get.url)
        model.createResource(url / "unixSocketResponse" / "mode").
          addProperty(RDF.value, obj.unix_socket_response_time.get.mode)
      }
      return model
    }
    private def write(model : Model, url : String, obj: MonitSystemInfo) : Model = {
      model.createResource(url / "dataCollected").
        addProperty(RDF.value, DateUtils.format(obj.data_collected))

      model.createResource(url / "cpuUsage" / "user").
        addProperty(RDF.value, "" + obj.cpu.user)

      model.createResource(url / "cpuUsage" / "system").
        addProperty(RDF.value, "" + obj.cpu.system)

      model.createResource(url / "cpuUsage" / "wait").
        addProperty(RDF.value, "" + obj.cpu.Wait)

      model.createResource(url / "load" / "average").
        addProperty(RDF.value, "" + obj.load_average.avg)

      model.createResource(url / "load" / "min").
        addProperty(RDF.value, "" + obj.load_average.min)

      model.createResource(url / "load" / "max").
        addProperty(RDF.value, "" + obj.load_average.max)

      model.createResource(url / "memory" / "totalUsage").
        addProperty(RDF.value, "" + obj.memory_usage)


      model.createResource(url / "memory" / "percentageUsage").
        addProperty(RDF.value, "" + obj.memory_usage_perc)

      model.createResource(url / "status").
        addProperty(RDF.value, obj.status.toString)

      model.createResource(url / "monitoringStatus").
        addProperty(RDF.value, obj.monitoring_status.toString)

      model.createResource(url / "swap" / "totalUsage").
        addProperty(RDF.`type`, "" + obj.swap_usage)

      model.createResource(url / "swap" / "percentageUsage").
        addProperty(RDF.`type`, "" + obj.swap_usage_perc)

      return model
    }
  }
}