package it.unibo.ing.rdf

import com.hp.hpl.jena.rdf.model.{RDFWriter, Model}
import spray.json.{JsValue, JsonWriter}
import scala.language.implicitConversions
import scala.annotation.implicitNotFound


/**
 * @author Antonio Murgia
 * @version 15/12/14
 * These implicits add conversions from any object to rdf (if the right conversion method is imported
 */
@implicitNotFound(msg = "Cannot find RdfWriter type class for ${T}")
trait RdfWriter[T] {
  def write(obj : T) : Model
}
object RdfWriter {
  implicit def func2Writer[T](f: (T) => Model): RdfWriter[T] = new RdfWriter[T] {
    def write(obj: T) = f(obj)
  }
}