package it.unibo.ing.monit.model

/**
 * Created by tmnd on 03/11/14.
 */

import java.util.Date
case class MonitSystemInfo(name : String,
                           status : MonitStatus,
                           monitoring_status : MonitMonitoringStatus,
                           load_average : LoadAverage,
                           cpu : CpuUsage,
                           memory_usage_perc : Double,
                           memory_usage : Int,
                           swap_usage : Int,
                           swap_usage_perc : Double,
                           data_collected : Date) extends MonitInfo{
  override def toString = {
    "name -> " + name + "\n"+
    "monitoring status -> " + monitoring_status + "\n"+
    "load average -> " + load_average + "\n"+
    "cpu -> " + cpu +"\n"+
    "memory usage perc -> " + memory_usage_perc + "%\n"+
    "memory usage -> " + memory_usage + "\n"+
    "swap usage perc -> " + swap_usage_perc + "\n"+
    "swap usage -> " + swap_usage + "\n"+
    "data collected -> " + data_collected + "\n"
  }
}
object MonitSystemInfo{
  def applyOpt(name : Option[String],
            status : Option[MonitStatus],
            monitoring_status : Option[MonitMonitoringStatus],
            load_average : Option[LoadAverage],
            cpu : Option[CpuUsage],
            memory_usage_perc : Option[Double],
            memory_usage : Option[Int],
            swap_usage : Option[Int],
            swap_usage_perc : Option[Double],
            data_collected : Option[Date]) : MonitSystemInfo =
    apply(name.get,
          status.get,
          monitoring_status.get,
          load_average.get,
          cpu.get,
          memory_usage_perc.get,
          memory_usage.get,
          swap_usage.get,
          swap_usage_perc.get,
          data_collected.get)
}
