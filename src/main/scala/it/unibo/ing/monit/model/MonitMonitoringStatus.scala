package it.unibo.ing.monit.model

/**
 * @author Antonio Murgia
 * @version 03/11/14
 * Enum to represent the type of the Monitoring status
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
