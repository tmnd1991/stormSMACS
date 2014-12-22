package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode

import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, GenericNodeConf}
import myUtils.DateUtils
import storm.scala.dsl.{Logging, StormBolt}

/**
 * @author Antonio Murgia
 * @version 12/12/2014
 */
class GenericNodePersisterBolt(fusekiEndpoint : FusekiNodeConf) extends StormBolt(List()) with Logging {
  override def execute(t: Tuple) ={
    t matchSeq{
      case Seq(node : GenericNodeConf, date : Date, sData : SigarMeteredData) => {
        val graphName = "<http://java.util.date/" + DateUtils.format(date) + ">"
        t.ack
      }
    }
  }
}
