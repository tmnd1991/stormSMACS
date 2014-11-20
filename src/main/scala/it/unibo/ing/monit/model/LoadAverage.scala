package it.unibo.ing.monit.model

/**
 * Created by tmnd on 03/11/14.
 */
case class LoadAverage(min : Double, avg : Double, max : Double){
  override def toString = "min = " + min + "% avg = " + avg + "% max = " + max + "%"
}