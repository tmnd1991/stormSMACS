package it.unibo.ing.utils

/**
 * Created by tmnd on 21/10/14.
 */
object TypeExtractor {
  def extract(x : Any) : String = {
    if (x.isInstanceOf[Boolean]) return "bool"
    if (x.isInstanceOf[Int]) return "integer"
    if (x.isInstanceOf[Float]) return "float"
    if (x.isInstanceOf[String]) return "string"
    if (x.isInstanceOf[java.util.Date]) return "date"
    throw new IllegalArgumentException
  }
}
