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
}