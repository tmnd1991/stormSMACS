package it.unibo.ing.stormsmacs.topologies.spouts.Typed

import backtype.storm.tuple.MessageId
import storm.scala.dsl.additions.Logging
import storm.scala.dsl.{StormSpout}

import backtype.storm.utils.Utils
import java.util.Date

/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Spout that gives the clock to the entire stormsmacs topology
 */

class TimerSpout(pollTime : Long) extends StormSpout(List("GraphName")) with Logging{
  override def nextTuple = {
    Utils.sleep(pollTime)
    val now = new Date()
    using msgId(now.getTime) emit (now)
  }
  override def ack(messageId: Any): Unit = {
    logger info "acked " + new Date(messageId.asInstanceOf[Long])
  }
  override def fail(messageId: Any) : Unit = {
    val dateToBeReplayed = new Date(messageId.asInstanceOf[Long])
    using msgId(dateToBeReplayed.getTime) emit (dateToBeReplayed)
  }
}
