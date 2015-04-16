package it.unibo.ing.stormsmacs.topologies.spouts.Typed

import storm.scala.dsl.{StormSpout}

import backtype.storm.utils.Utils
import java.util.Date

/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Spout that gives the clock to the entire stormsmacs topology
 */

class TimerSpout(pollTime : Long) extends StormSpout(List("GraphName")){
  override def nextTuple = {
    Utils.sleep(pollTime)
    val now = new Date()
    using msgId(now.getTime) emit (now)
  }
}
