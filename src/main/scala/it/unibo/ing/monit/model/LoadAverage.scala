package it.unibo.ing.monit.model

/**
 * @author Antonio Murgia
 * @version 03/11/14
 * Case class that represents the average load, as minimum, average and maximum
 */
case class LoadAverage(min : Double, avg : Double, max : Double){
  override def toString = "min = " + min + "% avg = " + avg + "% max = " + max + "%"
}