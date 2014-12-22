package it.unibo.ing.stormsmacs.topologies.spouts

/**
 * @author Antonio Murgia
 */

import backtype.storm.utils.Utils
import storm.scala.dsl.StormSpout
import java.util.Date

class TimerSpout(pollTime : Long) extends StormSpout(List("GraphName")){
  override def nextTuple() = {
    emit(new Date)
    Utils.sleep(pollTime)
  }
}