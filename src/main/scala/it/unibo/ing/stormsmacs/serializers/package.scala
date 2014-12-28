package it.unibo.ing.stormsmacs

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input

/**
 * @author Antonio Murgia
 * @version 28/12/14.
 */
package object serializers {
  implicit class RichKryo(kryo: Kryo){
    def readSomeObjectOrNone[T](input: Input, clazz: Class[T]) : Option[T] = {
      val read = kryo.readObjectOrNull[T](input,clazz)
      if (read == null)
        None
      else
        Some(read)
    }
  }
}
