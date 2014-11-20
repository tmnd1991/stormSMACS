package it.unibo.ing.monit.parsers

import it.unibo.ing.monit.model.{MonitResponseTime, MonitMonitoringStatus, MonitStatus, MonitProcessInfo}
import it.unibo.ing.utils.DateUtils

import scala.concurrent.duration.Duration

import java.util.Date
/**
 * Created by tmnd on 31/10/14.
 */
object ProcessParser extends Parser[MonitProcessInfo,String]{
  val dateFormat = "EEE MMM dd HH:mm:ss yyyy"
  val myDurationParser = DurationParser
  val myResponseTimeParse = ResponseTimeParser
  override def parse(s: String) = {
    //defining variables
    var name : Option[String] = None
    var status : Option[MonitStatus] = None
    var monitStatus : Option[MonitMonitoringStatus] = None
    var pid : Option[Int]= None
    var parent_pid : Option[Int] = None
    var uptime : Option[Duration] = None
    var children : Option[Int] = None
    var data_collected : Option[Date] = None
    var memory_percent : Option[Float] = None
    var memory_kilobytes : Option[Int] = None
    var cpu_percent : Option[Float] = None
    var memory_kilobytes_total : Option[Int] = None
    var memory_percent_total : Option[Float] = None
    var cpu_percent_total : Option[Float] = None
    var port_response_time : Option[MonitResponseTime] = None
    var unix_socket_response_time : Option[MonitResponseTime] = None
    for (line <- s.linesWithSeparators){
      val trimmedLine = line.replaceAll(" +"," ").trim
      val fields = trimmedLine.split(" ").toList
      if (trimmedLine.startsWith("Process"))
        name = Some(fields(1).replaceAll("'",""))
      else if (trimmedLine.startsWith("uptime"))
        uptime = myDurationParser.parseOption(fields.tail)
      else if (trimmedLine.startsWith("status"))
        status = Some(MonitStatus.values(fields(1)))
      else if (trimmedLine.startsWith("pid"))
        pid = Some(fields(1).toInt)
      else if (trimmedLine.startsWith("children"))
        children = Some(fields(1).toInt)
      else if (trimmedLine.startsWith("monitoring status"))
        monitStatus = Some(MonitMonitoringStatus.values(fields(2)))
      else if (trimmedLine.startsWith("parent pid"))
        parent_pid = Some(fields(2).toInt)
      else if (trimmedLine.startsWith("memory percent") && trimmedLine.indexOf("total") == -1){
        val percent = fields(2)
        memory_percent = Some(percent.substring(0,percent.length-1).toFloat)
      }
      else if (trimmedLine.startsWith("memory kilobytes") && trimmedLine.indexOf("total") == -1)
        memory_kilobytes = Some(fields(2).toInt)
      else if (trimmedLine.startsWith("cpu percent") && trimmedLine.indexOf("total") == -1){
        val percent = fields(2)
        cpu_percent = Some(percent.substring(0,percent.length-1).toFloat)
      }
      else if (trimmedLine.startsWith("memory kilobytes total"))
        memory_kilobytes_total = Some(fields(3).toInt)
      else if (trimmedLine.startsWith("memory percent total")){
        val percent = fields(3)
        memory_percent_total = Some(percent.substring(0,percent.length-1).toFloat)
      }
      else if(trimmedLine.startsWith("cpu percent total")){
        val percent = fields(3)
        cpu_percent_total = Some(percent.substring(0,percent.length-1).toFloat)
      }
      else if(trimmedLine.startsWith("data collected"))
            data_collected = Some(DateUtils.parse(fields(2)+" "+fields(3)+" "+fields(4)+" "+fields(5)+" "+fields(6),dateFormat))
      else if (trimmedLine.startsWith("port response time")){
        port_response_time = myResponseTimeParse.parseOption(fields.tail.tail.tail)
      }
      else if (trimmedLine.startsWith("unix socket response time")){
        unix_socket_response_time = myResponseTimeParse.parseOption(fields.tail.tail.tail.tail)//Some(x.trim)
      }
    }
    MonitProcessInfo.applyOpt(name,status,monitStatus,pid,parent_pid,
      uptime,children,memory_kilobytes,memory_kilobytes_total,
      memory_percent,memory_percent_total,cpu_percent,cpu_percent_total,
      data_collected,port_response_time,unix_socket_response_time)
  }
}
