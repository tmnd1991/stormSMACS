package it.unibo.ing.stormsmacs.topologies.bolts

import backtype.storm.tuple.Tuple
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.FusekiNode
import storm.scala.dsl.{Logging, StormBolt}

/**
 * Created by tmnd on 18/11/14.
 */
class CloudFoundryNodePersisterBolt(fusekiEndpoint : FusekiNode) extends StormBolt(List()) with Logging{
  override def execute(t: Tuple) = {
    val mData = t.getValue(0).asInstanceOf[SigarMeteredData]
    logger.debug(mData.toString)
  }
}