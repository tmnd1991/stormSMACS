package it.unibo.ing.stormsmacs.serializers

import java.util.Date

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import it.unibo.ing.monit.model._
import scala.concurrent.duration.Duration

/**
 * @author Antonio Murgia
 * @version 28/12/14.
 * Kryo serializer for MonitInfos to speed up communication in storm topologies.
 */
class MonitInfoSerializer extends Serializer[MonitInfo]{
  import spray.json._
  import it.unibo.ing.monit.model.JsonConversions._

  override def write(kryo: Kryo, output: Output, t: MonitInfo): Unit = output.writeString(t.toJson.compactPrint)

  override def read(kryo: Kryo, input: Input, aClass: Class[MonitInfo]): MonitInfo = input.readString().parseJson.convertTo[MonitInfo]
}
/*
class MonitProcessInfoSerializer extends Serializer[MonitProcessInfo]{

  override def write(kryo: Kryo, output: Output, t: MonitProcessInfo): Unit = {
    output.writeFloat(t.cpu_percent, 1000, true)
    output.writeInt(t.children, true)
    output.writeFloat(t.cpu_percent_total, 1000, true)
    kryo.writeObject(output, t.data_collected)
    output.writeInt(t.memory_kb, true)
    output.writeInt(t.memory_kb_total, true)
    output.writeFloat(t.memory_perc, 1000, true)
    output.writeFloat(t.memory_perc_total, 1000, true)
    output.writeString(t.monitoring_status.toString)
    output.writeString(t.name)
    output.writeInt(t.parent_pid, true)
    output.writeInt(t.pid, true)
    if (t.port_response_time.isDefined){
      output.writeString(t.port_response_time.get.d.toString)
      output.writeString(t.port_response_time.get.url)
      output.writeString(t.port_response_time.get.mode)
    }
    else{
      output.writeString("0 s")
      output.writeString("noURL")
      output.writeString("noMode")
    }
    output.writeString(t.status.toString)
    if (t.unix_socket_response_time.isDefined){
      output.writeString(t.unix_socket_response_time.get.d.toString)
      output.writeString(t.unix_socket_response_time.get.mode)
      output.writeString(t.unix_socket_response_time.get.url)
    }
    else{
      output.writeString("0 s")
      output.writeString("noMode")
      output.writeString("noURL")
    }
    output.writeString(t.uptime.toString)
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[MonitProcessInfo]): MonitProcessInfo = {
    MonitProcessInfo(
      cpu_percent = input.readFloat(1000, true),
      children = input.readInt(true),
      cpu_percent_total = input.readFloat(1000, true),
      data_collected = kryo.readObject[Date](input, classOf[Date]),
      memory_kb = input.readInt(true),
      memory_kb_total = input.readInt(true),
      memory_perc = input.readFloat(1000, true),
      memory_perc_total = input.readFloat(1000, true),
      monitoring_status = MonitMonitoringStatus.values(input.readString()),
      name = input.readString,
      parent_pid = input.readInt(true),
      pid = input.readInt(true),
      port_response_time = Some(MonitResponseTime(Duration(input.readString), input.readString(), input.readString())),
      status = MonitStatus.values(input.readString()),
      unix_socket_response_time = Some(MonitResponseTime(Duration(input.readString()), input.readString(), input.readString())),
      uptime = Duration(input.readString())
    )
  }
}

class MonitSystemInfoSerializer extends Serializer[MonitSystemInfo]{
  override def write(kryo: Kryo, output: Output, t: MonitSystemInfo): Unit = {
    output.writeString(t.name)
    output.writeString(t.status.toString)
    output.writeString(t.monitoring_status.toString)
    output.writeDouble(t.load_average.min, 1000, true)
    output.writeDouble(t.load_average.avg, 1000, true)
    output.writeDouble(t.load_average.max, 1000, true)
    output.writeDouble(t.cpu.user, 1000, true)
    output.writeDouble(t.cpu.system, 1000, true)
    output.writeDouble(t.cpu.Wait, 1000, true)
    output.writeDouble(t.memory_usage_perc, 1000, true)
    output.writeInt(t.memory_usage, true)
    output.writeInt(t.swap_usage, true)
    output.writeDouble(t.swap_usage_perc, 1000, true)
    kryo.writeObject(output, t.data_collected)
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[MonitSystemInfo]): MonitSystemInfo = {
    MonitSystemInfo(
      name = input.readString(),
      status = MonitStatus.values(input.readString()),
      monitoring_status = MonitMonitoringStatus.values(input.readString()),
      load_average = LoadAverage(input.readDouble(1000, true), input.readDouble(1000, true), input.readDouble(1000, true)),
      cpu = CpuUsage(input.readDouble(1000, true), input.readDouble(1000, true), input.readDouble(1000, true)),
      memory_usage_perc = input.readDouble(1000, true),
      memory_usage = input.readInt(true),
      swap_usage = input.readInt(true),
      swap_usage_perc = input.readDouble(1000, true),
      data_collected = kryo.readObject[Date](input, classOf[Date])
    )
  }
}
*/