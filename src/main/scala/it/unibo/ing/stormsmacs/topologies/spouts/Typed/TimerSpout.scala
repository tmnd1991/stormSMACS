package it.unibo.ing.stormsmacs.topologies.spouts.Typed

import storm.scala.dsl.TypedSpout

import backtype.storm.utils.Utils
import java.util.Date

/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Spout that gives the clock to the entire stormsmacs topology
 */

class TimerSpout(pollTime : Long) extends TypedSpout[Tuple1[Date]](false,"GraphName"){
  override def nextTuple = {
    emit(new Date())
    Utils.sleep(pollTime)
  }
}
