package it.unibo.ing.sigar.restful.model

import com.hp.hpl.jena.rdf.model.Model
import spray.json.DefaultJsonProtocol

/**
 * @author Antonio Murgia
 * @version 28/10/14.
 */
case class SigarMeteredData(cpuPercent : Double,
                            freeMemPercent : Double,
                            diskReads : Long,
                            diskWrites : Long,
                            diskReadBytes : Long,
                            diskWriteBytes : Long,
                            netInBytes : Long,
                            netOutBytes : Long,
                            processes : Long,
                            uptime : Double,
                            numberOfCores : Int,
                            osName : String,
                            cpuName : String)

object SigarMeteredDataFormat extends DefaultJsonProtocol{
  implicit val SigarMeteredDataJsonFormat = jsonFormat13(SigarMeteredData)
}
