package it.unibo.ing.stormsmacs.topologies.reliable.spouts

import java.util.concurrent.{LinkedBlockingQueue, BlockingQueue, TimeUnit, Executors}
import backtype.storm.Config

import scala.concurrent._
import ExecutionContext.Implicits.global
import it.unibo.ing.stormsmacs.topologies.facilities.{FailHandler, DefaultFailHandler}
import storm.scala.dsl.additions.Logging
import storm.scala.dsl.{StormSpout}


import java.util.Date



/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Spout that gives the clock to the entire stormsmacs topology
 */

class TimerSpout(pollTime : Long) extends StormSpout(List("GraphName")) with Logging{
  private var failHandler : FailHandler[Long] = _
  private var queue : BlockingQueue[Long] = _
  setup{
    failHandler = new DefaultFailHandler(2)
    queue = new LinkedBlockingQueue[Long]()
    val runnable = new Runnable() {
      override def run() = queue.put(System.currentTimeMillis())
    }
    val service = Executors.newSingleThreadScheduledExecutor()
    service.scheduleAtFixedRate(runnable, 0, pollTime, TimeUnit.MILLISECONDS)
  }
  override def nextTuple = Future{
    val now = new Date(queue.take())
    using.msgId(now.getTime).emit(now)
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
  override def getComponentConfiguration() : java.util.Map[String, Object]= {
    val conf = new Config()
    conf.setMaxTaskParallelism(1)
    conf
  }
}