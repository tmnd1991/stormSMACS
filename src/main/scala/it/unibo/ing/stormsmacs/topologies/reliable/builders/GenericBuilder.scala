package it.unibo.ing.stormsmacs.topologies.reliable.builders

import java.util.Date

import backtype.storm.topology.TopologyBuilder
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.{VirtuosoNodeConf, FusekiNodeConf, GenericNodeConf, PersisterNodeConf}
import it.unibo.ing.stormsmacs.topologies.facilities.StormSmacsBuilder
import it.unibo.ing.stormsmacs.topologies.reliable.bolts.Generic.{GenericPersisterVirtuosoBolt, GenericPersisterFusekiBolt, GenericClientBolt}
import it.unibo.ing.stormsmacs.topologies.reliable.spouts.TimerSpout
import scala.language.postfixOps

/**
 * @author Antonio Murgia
 * @version 08/03/15
 * Adds to the current topology all the spouts and bolts needed to monitor the list of generic node passed
 */
class GenericBuilder(pollTime : Long,
                     persisterNode : PersisterNodeConf,
                     list: Seq[GenericNodeConf],
                     timerSpoutName : String,
                     maxNodesPerTask : Int = 3) extends StormSmacsBuilder{
  override def build(builder: TopologyBuilder): TopologyBuilder = {
    if (list.nonEmpty){
      val persisterTasks =  calctasks(list.size, maxNodesPerTask)
      val boltReaderName = "genericReader"
      val boltPersisterName = "genericPersister"
      val persisterBolt = persisterNode match{
        case x : FusekiNodeConf => new GenericPersisterFusekiBolt(x)
        case x : VirtuosoNodeConf => new GenericPersisterVirtuosoBolt(x)
      }
      val persisterDeclarer = builder.setBolt(boltPersisterName,persisterBolt,persisterTasks)
      for(gn <- list){
        val name = boltReaderName + " [" + gn.id + "] "
        builder.setBolt(name, new GenericClientBolt(gn, pollTime)).allGrouping(timerSpoutName)
        persisterDeclarer.shuffleGrouping(name)
      }
    }
    builder
  }
  private def calctasks(nodes : Int, arity : Int): Int = (math ceil (nodes.toFloat / arity)) toInt
}
