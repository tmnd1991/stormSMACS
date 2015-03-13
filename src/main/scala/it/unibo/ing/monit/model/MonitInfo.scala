package it.unibo.ing.monit.model


/**
 * @author Antonio Murgia
 * @version 03/11/14
 * abstract class to be inherited by MonitProcessInfo and MonitSystemInfo
 */
abstract class MonitInfo{
  val name : String
  def resId : Int
}