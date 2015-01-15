package it.unibo.ing.stormsmacs.rdfBindings

/**
 * @author Murgia Antonio
 * @version 15/12/14
 */
import java.net.{URI, URL}
import com.hp.hpl.jena.graph.impl.SimpleGraphMaker
import com.hp.hpl.jena.rdf.model.{Model, ModelFactory}
import com.hp.hpl.jena.vocabulary.RDF
import it.unibo.ing.monit.model.{MonitSystemInfo, MonitProcessInfo, MonitInfo}
import it.unibo.ing.rdf._
import it.unibo.ing.rdf.RdfWriter
import it.unibo.ing.utils._

case class CFNodeData(url : URL, info : MonitInfo) {
}
object CFNodeDataRdfConversion{
  import scala.collection.JavaConversions._

  implicit object MonitProcessInfoRdfWriter extends RdfWriter[MonitProcessInfo] {
    override def write(obj: MonitProcessInfo, absPath: String): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      val r = m.createResource((new URI(absPath) / obj.name).toString)
      r.addProperty(Properties.status, obj.status.toString)
      r.addProperty(Properties.dataCollected, DateUtils.format(obj.data_collected))
      r.addProperty(Properties.children, obj.children.toString)
      r.addProperty(Properties.monitoringStatus, obj.monitoring_status.toString)
      r.addProperty(Properties.parentPid, obj.parent_pid.toString)
      r.addProperty(Properties.pid, obj.pid.toString)
      r.addProperty(Properties.uptimeMs, obj.uptime.toMillis.toString)
      if (obj.port_response_time.isDefined){
        r.addProperty(Properties.portResponseTime, obj.port_response_time.get.d.toMillis.toString)
        r.addProperty(Properties.portResponseDestination, obj.port_response_time.get.url)
        r.addProperty(Properties.portResponseMode, obj.port_response_time.get.mode)
      }
      r.addProperty(Properties.totalCPUperc, obj.cpu_percent_total.toString)
      r.addProperty(Properties.totalMemoryKb, obj.memory_kb_total.toString)
      r.addProperty(Properties.totalMemoryPerc, obj.memory_perc.toString)
      if (obj.unix_socket_response_time.isDefined){
        r.addProperty(Properties.unixSocketResponseTime, obj.unix_socket_response_time.get.d.toMillis.toString)
        r.addProperty(Properties.unixSocketResponseDestination, obj.unix_socket_response_time.get.url)
        r.addProperty(Properties.unixSocketResponseMode, obj.unix_socket_response_time.get.mode)
      }
      r.addProperty(Properties.CPUPercentageUsage, obj.cpu_percent.toString)
      r.addProperty(Properties.memoryPercUsage, obj.memory_perc.toString)
      m
    }
  }

  implicit object MonitSystemInfoRdfWriter extends RdfWriter[MonitSystemInfo]{
    override def write(obj: MonitSystemInfo, absPath: String): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      val r = m.createResource((new URI(absPath) / obj.name).toString)
      r.addProperty(Properties.dataCollected, DateUtils.format(obj.data_collected))
      r.addProperty(Properties.cpuUsageUser, obj.cpu.user.toString)
      r.addProperty(Properties.cpuUsageSystem, obj.cpu.system.toString)
      r.addProperty(Properties.cpuUsageWait, obj.cpu.Wait.toString)

      r.addProperty(Properties.averageLoad, obj.load_average.avg.toString)
      r.addProperty(Properties.minLoad, obj.load_average.min.toString)
      r.addProperty(Properties.maxLoad, obj.load_average.max.toString)
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
      val resUri = obj.url.toString / "CF"
      val r = model.createResource(resUri)
      r.addProperty(RDF.`type`, "Cloudfoundry Node")
      val infoM = obj.info.toRdf(resUri)
      model.setNsPrefixes(infoM.getNsPrefixMap)
      model.add(infoM)
      model
    }
  }


}