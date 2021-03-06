package it.unibo.ing.stormsmacs.serializers

import java.net.URL

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.twitter.chill.ScalaKryoInstantiator
import it.unibo.ing.stormsmacs.conf.GenericNodeConf

/**
 * @author Antonio Murgia
 * @version 28/12/14.
 * Kryo serializer for GenericNodeConf to speed up communication in storm topologies.
 */
class GenericNodeConfSerializer extends Serializer[GenericNodeConf]{
  import spray.json._
  import it.unibo.ing.stormsmacs.conf.GenericNodeProtocol._
  //val instantiator = new ScalaKryoInstantiator()
  override def write(kryo: Kryo, output: Output, t: GenericNodeConf): Unit = {
    output.writeInt(t.connectTimeout, true)
    output.writeInt(t.readTimeout,true)
    output.writeString(t.id)
    output.writeString(t.url.toString)
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[GenericNodeConf]): GenericNodeConf = {
    GenericNodeConf(
      connect_timeout = input.readInt(true),
      read_timeout = input.readInt(true),
      id = input.readString(),
      url = new URL(input.readString())
    )
  }
}