package it.unibo.ing.stormsmacs.topologies.bolts

import backtype.storm.tuple.Tuple
import storm.scala.dsl.{Logging, StormBolt}

/**
 * Created by tmnd on 24/11/14.
 */
class PrintBolt extends StormBolt(List()) with Logging{
  override def execute(t: Tuple) = {
    val s = t.getValueByField("GraphName").asInstanceOf[java.util.Date].toString
    emitData(t,s)
  }
  def emitData(t : Tuple, s: String) = {
    using anchor t emit s
    logger.info(s)
  }
}
