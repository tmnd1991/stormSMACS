package it.unibo.ing.stormsmacs.serializers

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import java.net.URL
/**
 * @author Antonio Murgia
 * @version 28/12/14.
 */
class OpenStackNodeConfSerializer extends Serializer[OpenStackNodeConf]{
  override def write(kryo: Kryo, output: Output, t: OpenStackNodeConf): Unit = {
    output.writeInt(t.connectTimeout, true)
    output.writeInt(t.readTimeout, true)
    output.writeString(t.ceilometerUrl.toString)
    output.writeString(t.id)
    output.writeString(t.keystoneUrl.toString)
    output.writeString(t.password)
    output.writeString(t.tenantName)
    output.writeString(t.username)
  }

  override def read(kryo: Kryo, input: Input, aClass: Class[OpenStackNodeConf]): OpenStackNodeConf = {
    OpenStackNodeConf.apply(
      connect_timeout = Some(input.readInt(true)),
      read_timeout = Some(input.readInt(true)),
      ceilometerUrl = new URL(input.readString()),
      id = input.readString(),
      keystoneUrl = new URL(input.readString()),
      password = input.readString(),
      tenantName = input.readString(),
      username = input.readString()
    )
  }
}