package it.unibo.ing.stormsmacs.serializers

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import org.openstack.api.restful.ceilometer.v2.elements.Statistics
import java.util.Date

/**
 * @author Antonio Murgia
 * @version 27/12/2014
 */

class StatisticsSerializer extends Serializer[Statistics]{
  override def write(kryo: Kryo, output: Output, t: Statistics): Unit ={
    kryo.writeObjectOrNull(output, t.aggregate.orNull, classOf[Map[String,Float]])
    output.writeFloat(t.avg)
    output.writeInt(t.count,true)
    output.writeFloat(t.duration, 100, true)
    kryo.writeObject(output, t.duration_end)
    kryo.writeObject(output, t.duration_start)
    kryo.writeObjectOrNull(output,t.groupby.orNull, classOf[Map[String,String]])
    output.writeFloat(t.max)
    output.writeFloat(t.min)
    output.writeInt(t.period, true)
    kryo.writeObject(output, t.period_end)
    kryo.writeObject(output, t.period_start)
    output.writeFloat(t.sum)
    output.writeString(t.unit)
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[Statistics]): Statistics ={
    Statistics(
      aggregate = kryo.readSomeObjectOrNone[Map[String,Float]](input, classOf[Map[String,Float]]),
      avg = input.readFloat(),
      count = input.readInt(true),
      duration = input.readFloat(100, true),
      duration_end = kryo.readObject[Date](input, classOf[Date]),
      duration_start = kryo.readObject[Date](input, classOf[Date]),
      groupby = kryo.readSomeObjectOrNone[Map[String,String]](input, classOf[Map[String,String]]),
      max = input.readFloat(),
      min = input.readFloat(),
      period = input.readInt(true),
      period_end = kryo.readObject[Date](input, classOf[Date]),
      period_start = kryo.readObject[Date](input, classOf[Date]),
      sum = input.readFloat(),
      unit = input.readString()
    )
  }
}