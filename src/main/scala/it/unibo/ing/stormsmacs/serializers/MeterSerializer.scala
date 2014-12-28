package it.unibo.ing.stormsmacs.serializers

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import org.openstack.api.restful.ceilometer.v2.elements.{MeterType, Meter}

/**
 * @author Antonio Murgia
 * @version 28/12/14.
 */
class MeterSerializer extends Serializer[Meter]{

  override def write(kryo: Kryo, output: Output, t: Meter): Unit = {
    output.writeString(t.meter_id)
    output.writeString(t.name)
    output.writeString(t.project_id)
    output.writeString(t.resource_id)
    output.writeString(t.source)
    output.writeString(t.`type`.toString())
    output.writeString(t.unit)
    output.writeString(t.user_id)
  }
  override def read(kryo: Kryo, input: Input, aClass: Class[Meter]): Meter = {
    Meter(
      meter_id = input.readString(),
      name = input.readString(),
      project_id = input.readString(),
      resource_id = input.readString(),
      source = input.readString(),
      `type` = MeterType.values(input.readString()),
      unit = input.readString(),
      user_id = input.readString()
    )
  }
}
