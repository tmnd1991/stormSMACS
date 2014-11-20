package it.unibo.ing.monit.model

/**
 * Created by tmnd on 31/10/14.
 */
class MonitStatus(val value : String){
  override val toString = value
}
object MonitStatus{
  val values = Map(RUNNING.value -> RUNNING,
                   NOT_MONITORED.value -> NOT_MONITORED)
  object RUNNING extends MonitStatus("running")
  object NOT_MONITORED extends MonitStatus("not monitored")
}
