package it.unibo.ing.stormsmacs.serializers

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.twitter.chill.ScalaKryoInstantiator
import org.openstack.api.restful.ceilometer.v2.elements.Sample

/**
 * Created by tmnd91 on 25/03/15.
 */
class SampleSerializer extends Serializer[Sample]{
  import spray.json._
  import org.openstack.api.restful.ceilometer.v2.elements.JsonConversions._
  //val instantiator = new ScalaKryoInstantiator()
  override def write(kryo: Kryo, output: Output, t: Sample): Unit = {
    output.writeString(t.toJson.compactPrint)
    //instantiator.newKryo().writeObject(output,t)
  }
  override def read(kryo: Kryo, input: Input, aClass: Class[Sample]): Sample = {
    input.readString().parseJson.convertTo[Sample]
    //instantiator.newKryo().readObject(input,aClass)
  }
}
