package it.unibo.ing.utils

import java.net.URL

/**
 * Created by tmnd on 10/10/14.
 */
object URLUtils {
  def apply(s : String) = try{
    Some(new URL(s))
  }
  catch{
    case e : Throwable => None
  }
}
