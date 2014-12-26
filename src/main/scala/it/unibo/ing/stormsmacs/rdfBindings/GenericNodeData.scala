package it.unibo.ing.stormsmacs.rdfBindings

import it.unibo.ing.sigar.restful.model.SigarMeteredData
import java.net.URL

/**
 * @author Antonio Murgia
 * @version 26/12/14.
 */
case class GenericNodeData(url : URL, info : SigarMeteredData)

object GenericNodeDataRdfFormat{
  import it.unibo.ing.rdf.RdfWriter
  import it.unibo.ing.rdf.Properties
  import com.hp.hpl.jena.rdf.model._
  import com.hp.hpl.jena.vocabulary.RDF
  implicit object GenericNodeDataRdfWriter extends RdfWriter[GenericNodeData] {
    override def write(obj: GenericNodeData, absPath: String): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      val r = m.createResource(obj.url.toString)
      r.addProperty(RDF.`type`, "Generic Node")
      r.addProperty(Properties.cpuName, obj.info.cpuName)
      r.addProperty(Properties.CPUPercentageUsage, "" + obj.info.cpuPercent.toInt)
      r.addProperty(Properties.bytesReadFromDisk, "" + obj.info.diskReadBytes)
      r.addProperty(Properties.readsFromDisk, "" + obj.info.diskReads)
      r.addProperty(Properties.bytesWroteToDisk, "" + obj.info.diskWriteBytes)
      r.addProperty(Properties.writesToDisk, "" + obj.info.diskWrites)
      r.addProperty(Properties.freeMemPercentage, "" + obj.info.freeMemPercent)
      r.addProperty(Properties.bytesReadFromNet, "" + obj.info.netInBytes)
      r.addProperty(Properties.bytesWroteToNet, "" + obj.info.netOutBytes)
      r.addProperty(Properties.coreNumber, "" + obj.info.numberOfCores)
      r.addProperty(Properties.osName, "" + obj.info.osName)
      r.addProperty(Properties.numberOfProcesses, "" + obj.info.processes)
      r.addProperty(Properties.uptime, "" + obj.info.uptime)
      m
    }
  }
}