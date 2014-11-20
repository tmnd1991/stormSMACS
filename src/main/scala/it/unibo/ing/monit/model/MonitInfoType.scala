package it.unibo.ing.monit.model

/**
 * Created by tmnd on 01/11/14.
 */
abstract class MonitInfoType(val value : String)

object MonitInfoType{
  val values = Map(PROCESS.value -> PROCESS,
                   SYSTEM.value -> SYSTEM)
  object PROCESS extends MonitInfoType("Process")
  object SYSTEM extends MonitInfoType("System")
}
