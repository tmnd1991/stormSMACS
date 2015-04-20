package it.unibo.ing.stormsmacs.topologies.unreliable.spouts

import java.util.Date

import backtype.storm.utils.Utils
import storm.scala.dsl.StormSpout
import storm.scala.dsl.additions.Logging

/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Spout that gives the clock to the entire stormsmacs topology
 */

class TimerSpout(pollTime : Long) extends StormSpout(List("GraphName")) with Logging{
  override def nextTuple = {
    Utils.sleep(pollTime)
    val now = new Date()
    emit(now)
  }
}