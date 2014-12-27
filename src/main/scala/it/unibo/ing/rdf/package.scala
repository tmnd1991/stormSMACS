package it.unibo.ing


import java.io.ByteArrayOutputStream

import com.hp.hpl.jena.rdf.model.Model

import scala.language.implicitConversions

/**
 * Created by tmnd on 15/12/14.
 */

package object rdf {
  def serializationError(msg: String) = throw new SerializationException(msg)

  def jsonWriter[T](implicit writer: RdfWriter[T]) = writer

  implicit def pimpAny[T](any: T) = new PimpedAny(any)

  implicit class RichModel(m : Model){
    def rdfSerialization(language : String = null) : String = {
      val boa = new ByteArrayOutputStream()
      m.write(boa, language)
      val s = new String(boa.toByteArray, "UTF8")
      boa.close()
      s
    }
  }
}

package rdf {

  import com.hp.hpl.jena.rdf.model.Model

  class SerializationException(msg: String) extends RuntimeException(msg)

  private[rdf] class PimpedAny[T](any: T) {
    def toRdf(absPath : String = "")(implicit writer: RdfWriter[T]) : Model = writer.write(any, absPath)
  }
}