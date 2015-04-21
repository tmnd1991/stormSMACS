package it.unibo.ing.stormsmacs.serializers

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.twitter.chill.ScalaKryoInstantiator
import it.unibo.ing.stormsmacs.topologies.facilities.{DefaultFailHandler, FailHandler}

/**
 * Created by tmnd91 on 21/04/15.
 */
class DefaultFailHandlerSerializer extends Serializer[DefaultFailHandler]{
  val instantiator = new ScalaKryoInstantiator()
  val myKryo = instantiator.newKryo()

  override def write(kryo: Kryo, output: Output, t: DefaultFailHandler): Unit = myKryo.writeObject(output, t)

  override def read(kryo: Kryo, input: Input, aClass: Class[DefaultFailHandler]): DefaultFailHandler = myKryo.readObject(input,aClass)
}
