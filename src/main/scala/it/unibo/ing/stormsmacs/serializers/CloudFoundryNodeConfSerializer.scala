package it.unibo.ing.stormsmacs.serializers

import java.net.URL

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import it.unibo.ing.stormsmacs.conf.CloudFoundryNodeConf

/**
 * @author Antonio Murgia
 * @version 28/12/14.
 * Kryo serializer for CloudFoundryNodeConf to speed up communication in storm topologies.
 */
class CloudFoundryNodeConfSerializer extends Serializer[CloudFoundryNodeConf]{
  override def write(kryo: Kryo, output: Output, t: CloudFoundryNodeConf): Unit ={
    output.writeInt(t.connectTimeout, true)
    output.writeInt(t.readTimeout,true)
    output.writeString(t.id)
    output.writeString(t.url.toString)
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[CloudFoundryNodeConf]): CloudFoundryNodeConf = {
    CloudFoundryNodeConf(
      connect_timeout = Some(input.readInt(true)),
      read_timeout = Some(input.readInt(true)),
      id = input.readString(),
      url = new URL(input.readString())
    )
  }
}
