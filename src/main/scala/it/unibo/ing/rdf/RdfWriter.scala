package it.unibo.ing.rdf

import com.hp.hpl.jena.rdf.model.{RDFWriter, Model}
import spray.json.{JsValue, JsonWriter}
import scala.language.implicitConversions
import scala.annotation.implicitNotFound

/**
 * Created by tmnd on 15/12/14.
 */
@implicitNotFound(msg = "Cannot find RdfWriter type class for ${T}")
trait RdfWriter[T] {
  def write(obj : T, absPath : String = "") : Model
}
object RdfWriter {
  implicit def func2Writer[T](f: (T,String) => Model): RdfWriter[T] = new RdfWriter[T] {
    def write(obj: T, absPath : String = "") = f(obj, absPath)
  }
}