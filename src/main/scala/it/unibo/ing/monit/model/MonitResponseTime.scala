package it.unibo.ing.monit.model

/**
 * @author Antonio Murgia
 * @version 03/11/14
 * Representation of process duration
 */

import scala.concurrent.duration.Duration

case class MonitResponseTime(d : Duration, url : String, mode : String) {
  override def toString = d + " " + url.toString + " " + mode
}