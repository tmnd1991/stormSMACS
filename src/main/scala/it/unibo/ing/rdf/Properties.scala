package it.unibo.ing.rdf
import scala.collection.JavaConverters._
import com.hp.hpl.jena.rdf.model.ModelFactory
/**
 * Created by tmnd91 on 24/12/14.
 */
object Properties {
  private val m = ModelFactory.createDefaultModel()
  val NS : String = "http://ing.unibo.it/smacs/predicates#"
  val prefixes : java.util.Map[String, String] = Map("sp"->NS).asJava
  val memoryUsage            = m.createProperty(NS + "hasMemoryUsage")
  val memoryUsagePercentage  = m.createProperty(NS + "hasMemoryUsagePercentage")
  val averageLoad            = m.createProperty(NS + "averageLoad")
  val minLoad            = m.createProperty(NS + "minLoad")
  val maxLoad            = m.createProperty(NS + "maxLoad")
  val swapUsage              = m.createProperty(NS + "swapUsage")
  val swapUsagePercentage    = m.createProperty(NS + "swapUsagePercentage")
  val cpuUsage               = m.createProperty(NS + "cpuUsage")
  val cpuUsageUser           = m.createProperty(NS + "cpuUsageUser")
  val cpuUsageSystem         = m.createProperty(NS + "cpuUsageSystem")
  val cpuUsageWait           = m.createProperty(NS + "cpuUsageWait")
  val status                 = m.createProperty(NS + "status")
  val monitoringStatus       = m.createProperty(NS + "monitoringStatus")
  val pid                    = m.createProperty(NS + "pid")
  val parentPid              = m.createProperty(NS + "parentPid")
  val uptimeMs               = m.createProperty(NS + "uptimeMs")
  val children               = m.createProperty(NS + "children")
  val memoryKbUsage          = m.createProperty(NS + "memoryKbUsage")
  val totalMemoryKb          = m.createProperty(NS + "totalMemoryKb")
  val memoryPercUsage        = m.createProperty(NS + "memoryPercUsage")
  val totalMemoryPerc        = m.createProperty(NS + "totalMemoryPerc")
  val totalCPUperc           = m.createProperty(NS + "totalCPUperc")
  val dataCollected          = m.createProperty(NS + "dataCollected")
  val portResponseTime       = m.createProperty(NS + "portResponseTime")
  val portResponseDestination= m.createProperty(NS + "portResponseDestination")
  val portResponseMode       = m.createProperty(NS + "portResponseMode")
  val unixSocketResponseTime = m.createProperty(NS + "unixSocketResponseTime")
  val unixSocketResponseDestination = m.createProperty(NS + "unixSocketResponseDestination")
  val unixSocketResponseMode = m.createProperty(NS + "unixSocketResponseMode")
  val cpuName                = m.createProperty(NS + "cpuName")
  val percentageUsage     = m.createProperty(NS + "percentageUsage")
  val bytesReadFromDisk      = m.createProperty(NS + "bytesReadFromDisk")
  val readsFromDisk          = m.createProperty(NS + "readsFromDisk")
  val bytesWroteToDisk       = m.createProperty(NS + "bytesWroteToDisk")
  val writesToDisk           = m.createProperty(NS + "writesToDisk")
  val freeMemPercentage      = m.createProperty(NS + "freeMemPercentage")
  val bytesReadFromNet       = m.createProperty(NS + "bytesReadFromNet")
  val bytesWroteToNet        = m.createProperty(NS + "bytesWroteToNet")
  val coreNumber             = m.createProperty(NS + "coreNumber")
  val osName                 = m.createProperty(NS + "osName")
  val numberOfProcesses      = m.createProperty(NS + "numberOfProcesses")
  val sampleType             = m.createProperty(NS + "sampleType")
  val sampleId               = m.createProperty(NS + "sampleId")
  val projectId              = m.createProperty(NS + "projectId")
  val recordedAt             = m.createProperty(NS + "recordedAt")
  val resourceId             = m.createProperty(NS + "resourceId")
  val source                 = m.createProperty(NS + "source")
  val timestamp              = m.createProperty(NS + "timestamp")
  val unit                   = m.createProperty(NS + "unit")
  val userId                 = m.createProperty(NS + "userId")
  val volume                 = m.createProperty(NS + "volume")
  val averageValue           = m.createProperty(NS + "averageValue")
  val countValue             = m.createProperty(NS + "countValue")
  val duration               = m.createProperty(NS + "duration")
  val durationEnd            = m.createProperty(NS + "durationEnd")
  val durationStart          = m.createProperty(NS + "durationStart")
  val maxValue               = m.createProperty(NS + "maxValue")
  val minValue               = m.createProperty(NS + "minValue")
  val period                 = m.createProperty(NS + "period")
  val periodEnd              = m.createProperty(NS + "periodEnd")
  val periodStart            = m.createProperty(NS + "periodStart")
  val sumValue               = m.createProperty(NS + "sumValue")
  val counterName            = m.createProperty(NS + "counterName")
  val counterType            = m.createProperty(NS + "counterType")
  val counterUnit            = m.createProperty(NS + "counterUnit")
  val counterVolume          = m.createProperty(NS + "counterVolume")
  val messageId              = m.createProperty(NS + "messageId")
  def newProperty(s : String) = m.createProperty(NS + s)
}


/*
INSERT DATA
{ GRAPH <http://example/bookStore> {
<http://192.168.1.10> <http://www.w3.org/2001/vcard-rdf/3.0#ADR> "bla" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasChildren> "4" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#dataCollected> "2014-12-24T10:22:30Z" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#unixSocketResponseTime> "None" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasParentPid> "1" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasMonitoringStatus> "monitored" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#portResponseTime> "None" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#totalCPUperc> "2.2" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasPid> "3" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#usingCPUperc> "2.1" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasUptime> "3 seconds" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#totalMemoryKb> "2000" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#usingMemoryPerc> "212.2" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#hasStatus> "running" .
<http://192.168.1.10/pippo> <http://ing.unibo.it/smacs/predicates#totalMemoryPerc> "212.2" . } }
 */