package it.unibo.ing.monit.parsers

/**
 * Created by tmnd on 03/11/14.
 */
trait Parser[T,K] {
  def parse(x : K) : T
  def parseOption(x : K) = {
    try{
      Some(parse(x))
    }
    catch{
      case t : Throwable => None
    }
  }
}
