package it.unibo.ing.stormsmacs.serializers

import com.esotericsoftware.kryo.io.{Input, Output}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.{PersisterNodeType, VirtuosoNodeConf, FusekiNodeConf, PersisterNodeConf}
import it.unibo.ing.stormsmacs.conf.PersisterNodeType.FusekiNodeType

/**
 * @author Antonio Murgia
 * @version 03/03/15
 * Kryo serializer for PersisterNodeConf (Fuseki and Virtuoso) to speed up communication in storm topologies.
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