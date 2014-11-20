package it.unibo.ing.monit.model

/**
 * Created by tmnd on 03/11/14.
 */

import java.net.URL

import scala.concurrent.duration.Duration
case class MonitResponseTime(d : Duration, url : String, mode : String) {
  override def toString = d + " " + url.toString + " " + mode
}