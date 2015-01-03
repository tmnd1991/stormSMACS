package it.unibo.ing.utils

/**
 * Created by tmnd on 03/11/14.
 */
import scala.language.implicitConversions

object PimpMyLib{
  implicit def StringToRichString(s : String) = new RichString(s)
  class RichString(s : String) {
    def mySubstring(beginIndex : Int, endIndex : Int) : String = {
      val rightEndIndex = if (endIndex < 0) s.length+endIndex
      else endIndex
      s.substring(beginIndex, rightEndIndex)
    }
  }
  import scala.collection.IterableLike
  import scala.collection.generic.CanBuildFrom

  implicit class RichCollection[A, Repr](xs: IterableLike[A, Repr]){
    def distinctBy[B, That](f: A => B)(implicit cbf: CanBuildFrom[Repr, A, That]) = {
      val builder = cbf(xs.repr)
      val i = xs.iterator
      var set = Set[B]()
      while (i.hasNext) {
        val o = i.next
        val b = f(o)
        if (!set(b)) {
          set += b
          builder += o
        }
      }
      builder.result
    }
  }
}