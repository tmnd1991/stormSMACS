package it.unibo.ing.monit.model

/**
 * Created by tmnd on 31/10/14.
 */
class MonitMonitoringStatus(val value : String){
  override val toString = value
}
object MonitMonitoringStatus{
  val values = Map(MONITORED.value -> MONITORED,
                   NOT_MONITORED.value -> NOT_MONITORED)
  object NOT_MONITORED extends MonitMonitoringStatus("not monitored")
  object MONITORED extends MonitMonitoringStatus("monitored")
}
