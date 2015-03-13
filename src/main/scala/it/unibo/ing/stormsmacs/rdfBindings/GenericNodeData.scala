package it.unibo.ing.stormsmacs.rdfBindings

import it.unibo.ing.sigar.restful.model.SigarMeteredData
import java.net.URL
import it.unibo.ing.utils._

/**
 * @author Antonio Murgia
 * @version 26/12/14
 * Converters from Generic node data to Rdf
 */
case class GenericNodeSample(url : URL, info : SigarMeteredData)
case class GenericNodeResource(url : URL, info : SigarMeteredData)
object GenericNodeDataRdfFormat{
  import it.unibo.ing.rdf.RdfWriter
  import it.unibo.ing.rdf.Properties
  import com.hp.hpl.jena.rdf.model._
  import com.hp.hpl.jena.vocabulary.RDF

  implicit object GenericNodeSampleRdfWriter extends RdfWriter[GenericNodeSample] {
    override def write(obj: GenericNodeSample): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      val url = URLUtils.removePort(obj.url)
      val cpuURL = (url / "cpu").toString
      val coreNumberURL = (url / "cpu" / "coreNumbers").toString
      val cpuPercentUsage = (url /  "cpu"  / "percentUsage").toString
      val diskByteReads = (url / "disk" / "bytesRead").toString
      val diskReads = (url / "disk" / "reads").toString
      val diskByteWrites = (url / "disk" / "bytesWrite").toString
      val diskWrites = (url / "disk" / "writes").toString
      val memoryFreePercentage = (url / "memory" / "freePercentage").toString
      val netBytesRead = (url / "net" / "bytesRead").toString
      val netBytesWrite = (url / "net" / "bytesWrite").toString
      val os = (url / "os").toString
      val numberOfProcesses = (url / "numberOfProcesses").toString
      val uptime = (url / "upTime").toString

      m.createResource(cpuURL).addProperty(Properties.name, obj.info.cpuName)

      m.createResource(coreNumberURL).
        addProperty(RDF.value, "" + obj.info.numberOfCores).
        addProperty(RDF.`type`, "Sample")

      m.createResource(cpuPercentUsage).
        addProperty(RDF.value, "" + obj.info.cpuPercent.toInt).
        addProperty(RDF.`type`, "Sample")

      m.createResource(diskByteReads).
        addProperty(RDF.value, "" + obj.info.diskReadBytes).
        addProperty(RDF.`type`, "Sample")

      m.createResource(diskReads).
        addProperty(RDF.value, "" + obj.info.diskReads).
        addProperty(RDF.`type`, "Sample")

      m.createResource(diskByteWrites).
        addProperty(RDF.value, "" + obj.info.diskWriteBytes).
        addProperty(RDF.`type`, "Sample")

      m.createResource(diskWrites).
        addProperty(RDF.value, "" + obj.info.diskWrites).
        addProperty(RDF.`type`, "Sample")

      m.createResource(memoryFreePercentage).
        addProperty(RDF.value, "" + obj.info.freeMemPercent).
        addProperty(RDF.`type`, "Sample")

      m.createResource(netBytesRead).
        addProperty(RDF.value, "" + obj.info.netInBytes).
        addProperty(RDF.`type`, "Sample")

      m.createResource(netBytesWrite).
        addProperty(RDF.value, "" + obj.info.netOutBytes).
        addProperty(RDF.`type`, "Sample")

      m.createResource(os).
        addProperty(RDF.value, "" + obj.info.osName).
        addProperty(RDF.`type`, "Sample")

      m.createResource(numberOfProcesses).
        addProperty(RDF.value, "" + obj.info.processes).
        addProperty(RDF.`type`, "Sample")

      m.createResource(uptime).
        addProperty(RDF.value, "" + obj.info.uptime).
        addProperty(RDF.`type`, "Sample")
      m
    }
  }
  implicit object GenericNodeResourceRdfWriter extends RdfWriter[GenericNodeResource]{
    override def write(obj: GenericNodeResource): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)

      val url = URLUtils.removePort(obj.url)
      val cpuURL = (url / "cpu").toString
      val coreNumberURL = (url / "cpu" / "coreNumbers").toString
      val cpuPercentUsage = (url /  "cpu"  / "percentUsage").toString
      val diskByteReads = (url / "disk" / "bytesRead").toString
      val diskReads = (url / "disk" / "reads").toString
      val diskByteWrites = (url / "disk" / "bytesWrite").toString
      val diskWrites = (url / "disk" / "writes").toString
      val memoryFreePercentage = (url / "memory" / "freePercentage").toString
      val netBytesRead = (url / "net" / "bytesRead").toString
      val netBytesWrite = (url / "net" / "bytesWrite").toString
      val os = (url / "os").toString
      val numberOfProcesses = (url / "numberOfProcesses").toString
      val uptime = (url / "upTime").toString


      m.createResource(coreNumberURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      m.createResource(cpuPercentUsage).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      m.createResource(diskByteReads).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "B")

      m.createResource(diskReads).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "units")

      m.createResource(diskByteWrites).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "B")

      m.createResource(diskWrites).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "units")

      m.createResource(memoryFreePercentage).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      m.createResource(netBytesRead).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "B")

      m.createResource(netBytesWrite).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "B")

      m.createResource(os).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      m.createResource(numberOfProcesses).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "units")

      m.createResource(uptime).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "cumulative").
        addProperty(Properties.unit, "ms")
      m
    }
  }
}