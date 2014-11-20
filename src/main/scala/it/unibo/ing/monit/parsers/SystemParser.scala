package it.unibo.ing.monit.parsers

import java.util.Date

import it.unibo.ing.monit.model._
import it.unibo.ing.utils.DateUtils

import scala.concurrent.duration.Duration
import it.unibo.ing.utils.PimpMyLib._

/**
 * Created by tmnd on 03/11/14.
 */
object SystemParser extends Parser[MonitSystemInfo,String]{
  val dateFormat = "EEE MMM dd HH:mm:ss yyyy"
  val myCpuUsageParser = CpuUsageParser
  val myLoadAverageParser = LoadAverageParser
  override def parse(s: String) = {
    //defining variables
    var name : Option[String] = None
    var status : Option[MonitStatus] = None
    var monitoring_status : Option[MonitMonitoringStatus] = None
    var load_average : Option[LoadAverage] = None
    var cpu : Option[CpuUsage] = None
    var memory_usage_perc : Option[Double] = None
    var memory_usage : Option[Int] = None
    var swap_usage : Option[Int] = None
    var swap_usage_perc : Option[Double] = None
    var data_collected : Option[Date] = None

    for (line <- s.linesWithSeparators){
      val trimmedLine = line.replaceAll(" +"," ").trim
      val fields = trimmedLine.split(" ").toList
      if (trimmedLine.startsWith("System"))
        name = Some(fields(1).replaceAll("'",""))
      else if (trimmedLine.startsWith("status"))
        status = Some(MonitStatus.values(fields(1)))
      else if (trimmedLine.startsWith("monitoring status"))
        monitoring_status = Some(MonitMonitoringStatus.values(fields(2)))
      else if(trimmedLine.startsWith("data collected"))
        data_collected = Some(DateUtils.parse(fields(2)+" "+fields(3)+" "+fields(4)+" "+fields(5)+" "+fields(6),dateFormat))
      else if(trimmedLine.startsWith("cpu"))
        cpu = myCpuUsageParser.parseOption(fields.tail)
      else if(trimmedLine.startsWith("load average"))
        load_average = myLoadAverageParser.parseOption(fields.tail.tail)
      else if(trimmedLine.startsWith("memory usage")){
        val values = fields.tail.tail
        memory_usage = try{
          Some(values.head.toInt)
        }
        catch{
          case t : Throwable => None
        }
        memory_usage_perc = Some(values(2).mySubstring(1,-2).toDouble)
      }
      else if (trimmedLine.startsWith("swap usage")){
        val values = fields.tail.tail
        swap_usage = try{
          Some(values.head.toInt)
        }
        catch{
          case t : Throwable => None
        }
        swap_usage_perc = Some(values(2).mySubstring(1,-2).toDouble)
      }
      else if (trimmedLine.startsWith("data collected")){
        data_collected = Some(DateUtils.parse(fields(2)+" "+fields(3)+" "+fields(4)+" "+fields(5)+" "+fields(6),dateFormat))
      }


    }
    MonitSystemInfo.applyOpt(name,status,monitoring_status,load_average,cpu,memory_usage_perc,memory_usage,swap_usage,swap_usage_perc,data_collected)
  }
}
