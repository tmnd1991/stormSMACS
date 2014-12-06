package it.unibo.ing.monit.model

/**
 * @author Antonio Murgia
 * @version 03/11/14
 */
case class CpuUsage(user : Double, system : Double, Wait : Double){
  override def toString = "us = " + user + "% sy = " + system + "% wa = " + Wait + "%"
}
