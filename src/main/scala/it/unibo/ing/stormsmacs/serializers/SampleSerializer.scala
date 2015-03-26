package it.unibo.ing.stormsmacs.serializers

import java.sql.Timestamp

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.twitter.chill.ScalaKryoInstantiator
import org.openstack.api.restful.ceilometer.v2.elements.{MeterType, Sample}

/**
 * Created by tmnd91 on 25/03/15.
 */
class SampleSerializer extends Serializer[Sample]{
  import spray.json._
  import org.openstack.api.restful.ceilometer.v2.elements.JsonConversions._
  val instantiator = new ScalaKryoInstantiator()
  val myKryo = instantiator.newKryo()
  override def write(kryo: Kryo, output: Output, t: Sample): Unit = {
    output.writeString(t.`type`.s)
    output.writeString(t.id)
    myKryo.writeObject(output,t.metadata)
    output.writeString(t.meter)
    output.writeString(t.project_id)
    kryo.writeObject(output,t.recorded_at)
    output.writeString(t.resource_id)
    output.writeString(t.source)
    kryo.writeObject(output,t.timestamp)
    output.writeString(t.unit)
    output.writeString(t.user_id)
    output.writeFloat(t.volume)
  }
  override def read(kryo: Kryo, input: Input, aClass: Class[Sample]): Sample = {
    Sample(`type` = MeterType.values(input.readString()),
           id = input.readString(),
            metadata = myKryo.readObject(input, classOf[Map[String,String]]),
            meter = input.readString(),
            project_id = input.readString(),
            recorded_at = kryo.readObject(input,classOf[Timestamp]),
            resource_id = input.readString(),
            source = input.readString(),
            timestamp = kryo.readObject(input,classOf[Timestamp]),
            unit = input.readString(),
            user_id = input.readString(),
            volume = input.readFloat()
    )
  }
}
