package it.unibo.ing.monit.model

/**
 * @author Antonio Murgia
 * @version 03/11/14
 */

import com.hp.hpl.jena.rdf.model.{ModelFactory, Model}
import it.unibo.ing.rdf.{Properties, RdfWriter}
import it.unibo.ing.utils.DateUtils

import scala.concurrent.duration._
import java.util.Date

case class MonitProcessInfo(name              : String,
                            status            : MonitStatus,
                            monitoring_status : MonitMonitoringStatus,
                            pid               : Int,
                            parent_pid        : Int,
                            uptime            : Duration,
                            children          : Int,
                            memory_kb         : Int,
                            memory_kb_total   : Int,
                            memory_perc       : Float,
                            memory_perc_total : Float,
                            cpu_percent       : Float,
                            cpu_percent_total : Float,
                            data_collected    : Date,
                            port_response_time: Option[MonitResponseTime],
                            unix_socket_response_time : Option[MonitResponseTime]) extends MonitInfo{
  override def toString = {
    "Process -> " + name + "\n"+
    "status -> " + status + "\n"+
    "monitoring status -> " + monitoring_status + "\n"+
    "pid -> " + pid + "\n"+
    "parent pid -> " + parent_pid + "\n"+
    "uptime -> " + uptime +"\n"+
    "children -> " + children +"\n"+
    "memory kilobytes -> " + memory_kb +"\n"+
    "memory kilobytes total -> " + memory_kb_total +"\n"+
    "memory percent -> " + memory_perc +"%\n"+
    "memory percent total -> " + memory_perc_total +"%\n"+
    "cpu percent -> " + cpu_percent +"%\n"+
    "cpu percent total -> " + cpu_percent_total +"%\n"+
    "data collected -> " + data_collected +"\n" + {
      if(port_response_time != None) "port reponse time -> "+ port_response_time.get
      else ""
    } +"\n" + {
      if(unix_socket_response_time != None) "unix_socket_response_time -> "+ unix_socket_response_time.get
      else ""
    }
  }
}
object MonitProcessInfo{
  def applyOpt(name              : Option[String],
            status            : Option[MonitStatus],
            monitoring_status : Option[MonitMonitoringStatus],
            pid               : Option[Int],
            parent_pid        : Option[Int],
            uptime            : Option[Duration],
            children          : Option[Int],
            memory_kb         : Option[Int],
            memory_kb_total   : Option[Int],
            memory_perc       : Option[Float],
            memory_perc_total : Option[Float],
            cpu_percent       : Option[Float],
            cpu_percent_total : Option[Float],
            data_collected    : Option[Date],
            port_response_time: Option[MonitResponseTime],
            unix_socket_response_time : Option[MonitResponseTime]) : MonitProcessInfo = {
    require(name != None)
    require(status != None)
    require(monitoring_status != None)
    require(pid != None)
    require(parent_pid != None)
    require(uptime != None)
    require(children != None)
    require(memory_kb != None)
    require(memory_kb_total != None)
    require(memory_perc != None)
    require(memory_perc_total != None)
    require(cpu_percent != None)
    require(cpu_percent_total != None)
    require(data_collected != None)
    apply(name.get,status.get,monitoring_status.get,pid.get,parent_pid.get,uptime.get,children.get,
          memory_kb.get,memory_kb_total.get,memory_perc.get,memory_perc_total.get,cpu_percent.get,
          cpu_percent_total.get,data_collected.get,port_response_time,unix_socket_response_time)
  }
}
