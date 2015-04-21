package it.unibo.ing.stormsmacs.topologies.reliable.spouts

import it.unibo.ing.stormsmacs.topologies.facilities.{FailHandler, DefaultFailHandler}
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
  private var failHandler : FailHandler[Long] = _
  setup{
    failHandler = new DefaultFailHandler(2)
  }
  override def nextTuple = {
    Utils.sleep(pollTime)
    val now = new Date()
    using msgId(now.getTime) emit (now)
  }
  override def ack(messageId: Any): Unit = {
    failHandler.acked(messageId.asInstanceOf[Long])
    logger info "acked " + new Date(messageId.asInstanceOf[Long])
  }
  override def fail(messageId: Any) : Unit = {
    val msgId = messageId.asInstanceOf[Long]
    val dateToBeReplayed = new Date(msgId)
    if (failHandler shouldBeReplayed msgId){
      logger info s"replay n ${failHandler.failCount(msgId)+1} $dateToBeReplayed"
      failHandler replayed msgId
      using msgId(dateToBeReplayed.getTime) emit (dateToBeReplayed)
    }
    else{
      logger info "not replayed " + dateToBeReplayed
      ack(messageId)
      failHandler acked msgId
    }
  }
}