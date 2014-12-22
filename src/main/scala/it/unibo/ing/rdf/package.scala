package it.unibo.ing


import scala.language.implicitConversions

/**
 * Created by tmnd on 15/12/14.
 */

package object rdf {
  def serializationError(msg: String) = throw new SerializationException(msg)

  def jsonWriter[T](implicit writer: RdfWriter[T]) = writer

  implicit def pimpAny[T](any: T) = new PimpedAny(any)
}

package rdf {

  import com.hp.hpl.jena.rdf.model.Model

  class SerializationException(msg: String) extends RuntimeException(msg)

  private[rdf] class PimpedAny[T](any: T) {
    def toRdf(implicit writer: RdfWriter[T]): Model = writer.write(any)
  }
}