package it.unibo.ing.stormsmacs.topologies.bolts

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.FusekiNode
import storm.scala.dsl.StormBolt
import storm.scala.dsl.Logging

/**
 * Created by tmnd on 18/11/14.
 */
class GenericNodePersisterBolt(fusekiEndpoint : FusekiNode) extends StormBolt(List()) with Logging {
  override def execute(p1: Tuple) = {
    logger.debug("receveid, nothing? " + (p1.getValue(0) == Nil))
  }
}
