package it.unibo.ing.monit.model

/**
 * @author Antonio Murgia
 * @version 03/11/14
 * Enum to represent the type of the Monit Info
 */
abstract class MonitInfoType(val value : String)

object MonitInfoType{
  val values = Map(PROCESS.value -> PROCESS,
                   SYSTEM.value -> SYSTEM)
  object PROCESS extends MonitInfoType("Process")
  object SYSTEM extends MonitInfoType("System")
}
