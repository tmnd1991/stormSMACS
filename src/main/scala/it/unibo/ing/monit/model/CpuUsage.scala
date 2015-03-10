package it.unibo.ing.monit.model

/**
 * @author Antonio Murgia
 * @version 03/11/14
 * Case class that represents the cpu usage, linux style (user %, system %, wait %)
 */
case class CpuUsage(user : Double, system : Double, Wait : Double){
  override def toString = "us = " + user + "% sy = " + system + "% wa = " + Wait + "%"
}
