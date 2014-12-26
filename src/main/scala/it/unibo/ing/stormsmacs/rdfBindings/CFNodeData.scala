package it.unibo.ing.stormsmacs.rdfBindings

/**
 * @author Murgia Antonio
 * @version 15/12/14
 */
import java.net.URL
import com.hp.hpl.jena.graph.impl.SimpleGraphMaker
import com.hp.hpl.jena.rdf.model.{Model, ModelFactory}
import com.hp.hpl.jena.vocabulary.RDF
import it.unibo.ing.monit.model.{MonitSystemInfo, MonitProcessInfo, MonitInfo}
import it.unibo.ing.rdf._
import it.unibo.ing.rdf.RdfWriter
import it.unibo.ing.utils.DateUtils

case class CFNodeData(url : URL, info : MonitInfo) {
}
object CFNodeDataRdfConversion{
  import scala.collection.JavaConversions._

  implicit object MonitProcessInfoRdfWriter extends RdfWriter[MonitProcessInfo] {
    override def write(obj: MonitProcessInfo, absPath: String): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      val r = m.createResource(absPath + "/" + obj.name)
      r.addProperty(Properties.status, obj.status.toString)
      r.addProperty(Properties.dataCollected, DateUtils.format(obj.data_collected))
      r.addProperty(Properties.children, obj.children.toString)
      r.addProperty(Properties.monitoringStatus, obj.monitoring_status.toString)
      r.addProperty(Properties.parentPid, obj.parent_pid.toString)
      r.addProperty(Properties.pid, obj.pid.toString)
      r.addProperty(Properties.uptime, obj.uptime.toString)
      r.addProperty(Properties.portResponseTime, obj.port_response_time.toString)
      r.addProperty(Properties.totalCPUperc, obj.cpu_percent_total.toString)
      r.addProperty(Properties.totalMemoryKb, obj.memory_kb_total.toString)
      r.addProperty(Properties.totalMemoryPerc, obj.memory_perc.toString)
      r.addProperty(Properties.unixSocketResponseTime, obj.unix_socket_response_time.toString)
      r.addProperty(Properties.CPUPercentageUsage, obj.cpu_percent.toString)
      r.addProperty(Properties.memoryPercUsage, obj.memory_perc.toString)
      m
    }
  }

  implicit object MonitSystemInfoRdfWriter extends RdfWriter[MonitSystemInfo]{
    override def write(obj: MonitSystemInfo, absPath: String): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      val r = m.createResource(absPath + "/" + obj.name)
      r.addProperty(Properties.dataCollected, DateUtils.format(obj.data_collected))
      r.addProperty(Properties.cpuUsage, obj.cpu.toString)
      r.addProperty(Properties.averageLoad, obj.load_average.toString)
      r.addProperty(Properties.memoryUsage, "" + obj.memory_usage)
      r.addProperty(Properties.memoryUsagePercentage, "" + obj.memory_usage_perc)
      r.addProperty(Properties.monitoringStatus, obj.monitoring_status.toString)
      r.addProperty(Properties.status, obj.status.toString)
      r.addProperty(Properties.swapUsage, "" + obj.swap_usage)
      r.addProperty(Properties.swapUsagePercentage, "" + obj.swap_usage_perc)
      m
    }
  }

  implicit object MonitInfoRdfWriter extends RdfWriter[MonitInfo]{
    override def write(obj: MonitInfo, abspath : String = ""): Model = {
      obj match{
        case m : MonitProcessInfo => m.toRdf(abspath)
        case m : MonitSystemInfo  => m.toRdf(abspath)
      }
    }
  }

  implicit object CFNodeDataRDFWriter extends RdfWriter[CFNodeData]{
    override def write(obj: CFNodeData, absPath: String): Model = {
      val graph = new SimpleGraphMaker().createGraph()
      val model = ModelFactory.createModelForGraph(graph)
      val resUri = obj.url.toString + "/CF"
      val r = model.createResource(resUri)
      r.addProperty(RDF.`type`, "Cloudfoundry Node")
      val infoM = obj.info.toRdf(resUri)
      model.setNsPrefixes(infoM.getNsPrefixMap)
      model.add(infoM)
      model
    }
  }


}