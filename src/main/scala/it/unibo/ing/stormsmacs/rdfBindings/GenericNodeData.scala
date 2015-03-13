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
      val cpuName = m.createResource((obj.url / "cpu").toString).addProperty(Properties.name, obj.info.cpuName)
      val cpuCores = m.createResource((obj.url / "cpu" / "coreNumbers").toString).
        addProperty(RDF.value, "" + obj.info.numberOfCores).
        addProperty(RDF.`type`, "Sample")
      val cpuUsage = m.createResource((obj.url /  "cpu"  / "percentUsage").toString).
        addProperty(RDF.value, "" + obj.info.cpuPercent.toInt).
        addProperty(RDF.`type`, "Sample")

      val diskByteRead = m.createResource((obj.url / "disk" / "bytesRead").toString).
        addProperty(RDF.value, "" + obj.info.diskReadBytes).
        addProperty(RDF.`type`, "Sample")

      val diskReads = m.createResource((obj.url / "disk" / "reads").toString).
        addProperty(RDF.value, "" + obj.info.diskReads).
        addProperty(RDF.`type`, "Sample")

      val diskByteWrite =  m.createResource((obj.url / "disk" / "bytesWrite").toString).
        addProperty(RDF.value, "" + obj.info.diskWriteBytes).
        addProperty(RDF.`type`, "Sample")

      val diskWrites =  m.createResource((obj.url / "disk" / "writes").toString).
        addProperty(RDF.value, "" + obj.info.diskWrites).
        addProperty(RDF.`type`, "Sample")

      val memory = m.createResource((obj.url / "memory" / "freePercentage").toString).
        addProperty(RDF.value, "" + obj.info.freeMemPercent).
        addProperty(RDF.`type`, "Sample")

      val netByteRead = m.createResource((obj.url / "net" / "bytesRead").toString).
        addProperty(RDF.value, "" + obj.info.netInBytes).
        addProperty(RDF.`type`, "Sample")

      val netByteWrite = m.createResource((obj.url / "net" / "bytesWrite").toString).
        addProperty(RDF.value, "" + obj.info.netOutBytes).
        addProperty(RDF.`type`, "Sample")

      val os = m.createResource((obj.url / "os").toString).
        addProperty(RDF.value, "" + obj.info.osName).
        addProperty(RDF.`type`, "Sample")

      val processes = m.createResource((obj.url / "numberOfProcesses").toString).
        addProperty(RDF.value, "" + obj.info.processes).
        addProperty(RDF.`type`, "Sample")

      val uptime = m.createResource((obj.url / "upTime").toString).
        addProperty(RDF.value, "" + obj.info.uptime).
        addProperty(RDF.`type`, "Sample")
      m
    }
  }
  implicit object GenericNodeResourceRdfWriter extends RdfWriter[GenericNodeResource]{
    override def write(obj: GenericNodeResource): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)

      val cpuCores = m.createResource((obj.url / "cpu" / "coreNumbers").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      val cpuUsage = m.createResource((obj.url / "cpu" / "percentUsage").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      val diskByteRead = m.createResource((obj.url / "disk" / "bytesRead").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "B")

      val diskReads = m.createResource((obj.url / "disk" / "reads").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      val diskByteWrite =  m.createResource((obj.url / "disk" / "bytesWrite").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "B")

      val diskWrites =  m.createResource((obj.url / "disk" / "writes").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      val memory = m.createResource((obj.url / "memory" / "freePercentage").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "%")

      val netByteRead = m.createResource((obj.url / "net" / "bytesRead").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "B")

      val netByteWrite = m.createResource((obj.url / "net" / "bytesWrite").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "B")

      val os = m.createResource((obj.url / "os").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge").
        addProperty(Properties.unit, "B")

      val processes = m.createResource((obj.url / "numberOfProcesses").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "gauge")

      val uptime = m.createResource((obj.url / "upTime").toString).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.sampleType, "cumulative").
        addProperty(Properties.unit, "ms")
      m
    }
  }
}