package it.unibo.ing.stormsmacs.serializers

import java.sql.Timestamp

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.twitter.chill.ScalaKryoInstantiator
import org.openstack.api.restful.ceilometer.v2.elements.Resource

/**
 * Created by tmnd91 on 25/03/15.
 */

class ResourceSerializer extends Serializer[Resource]{
  import spray.json._
  import org.openstack.api.restful.ceilometer.v2.elements.JsonConversions._
  val instantiator = new ScalaKryoInstantiator()
  override def write(kryo: Kryo, output: Output, t: Resource): Unit = {
    //instantiator.newKryo().writeObject(output,t)
    output.writeString(t.toJson.compactPrint)
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[Resource]): Resource = {
    //instantiator.newKryo().readObject(input,aClass)
    input.readString().parseJson.convertTo[Resource]
  }
}
