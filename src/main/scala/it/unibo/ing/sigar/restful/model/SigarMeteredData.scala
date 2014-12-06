package it.unibo.ing.sigar.restful.model

import spray.json.DefaultJsonProtocol

/**
  * @author Antonio Murgia
  * @version 28/10/14
  */
case class SigarMeteredData(cpuPercent : Double,
                             freeMemPercent : Double,
                             diskReads : Long,
                             diskWrites : Long,
                             diskReadBytes : Long,
                             diskWriteBytes : Long,
                             netInBytes : Long,
                             netOutBytes : Long,
                             processes : Long)

object SigarMeteredDataFormat extends DefaultJsonProtocol{
  implicit val SigarMeteredDataJsonFormat = jsonFormat9(SigarMeteredData)
}
