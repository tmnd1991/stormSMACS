package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.stormsmacs.conf.{CloudFoundryNodeConf, FusekiNodeConf}
import myUtils.DateUtils
import storm.scala.dsl.{Logging, StormBolt}

/**
 * @author Antonio Murgia
 * @version 12/12/2014
 */
class CloudFoundryNodePersisterBolt(fusekiEndpoint : FusekiNodeConf) extends StormBolt(List()) with Logging{
  override def execute(t: Tuple) = {
    t matchSeq {
      case Seq(node : CloudFoundryNodeConf, date: Date, mData: Seq[MonitInfo]) => {
        val graphName = "<http://java.util.date/" + DateUtils.format(date) + ">"
        logger.debug(mData.toString)
        t.ack
      }
    }
  }
}