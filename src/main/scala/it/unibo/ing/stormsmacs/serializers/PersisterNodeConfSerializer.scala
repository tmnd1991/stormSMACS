package it.unibo.ing.stormsmacs.serializers

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.{PersisterNodeType, VirtuosoNodeConf, FusekiNodeConf, PersisterNodeConf}
import it.unibo.ing.stormsmacs.conf.PersisterNodeType.FusekiNodeType

/**
 * Created by Andrea on 03/03/15.
 */

class PersisterNodeConfSerializer extends Serializer[PersisterNodeConf] {
  override def write(kryo: Kryo, output: Output, obj: PersisterNodeConf): Unit = {
    output.writeString(obj.`type`.value)
    obj.`type` match{
      case FusekiNodeType => write(kryo, output, obj.asInstanceOf[FusekiNodeConf])
      case virtuosoNodeType => write(kryo, output, obj.asInstanceOf[VirtuosoNodeConf])
    }
  }

  def write(kryo: Kryo, output: Output, obj: FusekiNodeConf): Unit = {
    output.writeString(obj.id)
    output.writeString(obj.url)
  }

  def write(kryo: Kryo, output: Output, obj: VirtuosoNodeConf): Unit = {
    output.writeString(obj.id)
    output.writeString(obj.url)
    output.writeString(obj.username)
    output.writeString(obj.password)
  }

  def readFuseki(kryo: Kryo, input: Input): PersisterNodeConf = {
    FusekiNodeConf(input.readString(),input.readString())
  }

  def readVirtuoso(kryo: Kryo, input: Input): PersisterNodeConf = {
    VirtuosoNodeConf(input.readString(),input.readString(),input.readString(),input.readString())
  }

  def read(kryo: Kryo, input: Input, `type`: Class[PersisterNodeConf]): PersisterNodeConf = {
    val tipo = PersisterNodeType.values(input.readString())
    tipo match{
      case FusekiNodeType => readFuseki(kryo, input)
      case virtuosoNodeType => readVirtuoso(kryo, input)
    }
  }
}
class FusekiNodeConfSerializer extends Serializer[FusekiNodeConf]{
  override def write(kryo: Kryo, output: Output, obj: FusekiNodeConf): Unit = kryo.writeObject(output, obj)

  override def read(kryo: Kryo, input: Input, `type`: Class[FusekiNodeConf]): FusekiNodeConf = kryo.readObject(input,classOf[PersisterNodeConf]).asInstanceOf[FusekiNodeConf]
}
class VirtuosoNodeConfSerializer extends Serializer[VirtuosoNodeConf]{
  override def write(kryo: Kryo, output: Output, obj: VirtuosoNodeConf): Unit = kryo.writeObject(output, obj)

  override def read(kryo: Kryo, input: Input, `type`: Class[VirtuosoNodeConf]): VirtuosoNodeConf = kryo.readObject(input,classOf[PersisterNodeConf]).asInstanceOf[VirtuosoNodeConf]
}