package it.unibo.ing.stormsmacs.topologies.bolts.Typed

import storm.scala.dsl.TypedSpout

import backtype.storm.utils.Utils
import java.util.Date

/**
 * @author Antonio Murgia
 * @version 22/12/2014
 */

class TimerSpout(pollTime : Long) extends TypedSpout[Tuple1[Date]](false,"GraphName"){
  override def nextTypedTuple: List[Tuple1[Date]] = {
    Utils.sleep(pollTime)
    List(Tuple1(new Date))
  }
}
