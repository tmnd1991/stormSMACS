package it.unibo.ing.stormsmacs.topologies.bolts

import backtype.storm.tuple.Tuple
import storm.scala.dsl.{Logging, StormBolt}

/**
 * Created by tmnd on 24/11/14.
 */
class PrintBolt extends StormBolt(List()) with Logging{
  override def execute(t: Tuple) = {
    logger.info(t.getValueByField("GraphName").asInstanceOf[java.util.Date].toString)
  }
}
