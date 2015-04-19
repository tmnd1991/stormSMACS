package it.unibo.ing.stormsmacs.topologies.builders

import java.util.Date

import backtype.storm.topology.TopologyBuilder
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, PersisterNodeConf, VirtuosoNodeConf, CloudFoundryNodeConf}
import it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed.{CloudFoundryNodePersisterVirtuosoBolt, CloudFoundryNodePersisterFusekiBolt, CloudFoundryNodeClientBolt}
import it.unibo.ing.stormsmacs.topologies.spouts.Typed.TimerSpout
import scala.language.postfixOps

/**
 * @author Antonio Murgia
 * @version 08/03/15
 * Adds to the current topology all the spouts and bolts needed to monitor the list of cloudfoundry node passed
 */
class CloudFoundryBuilder(pollTime : Long,
                          persister: PersisterNodeConf,
                          list: Seq[CloudFoundryNodeConf],
                          timerSpout : TimerSpout,
                          timerSpoutName : String,
                          maxNodesPerTask : Int = 3) extends StormSmacsBuilder{
  override def build(builder: TopologyBuilder): TopologyBuilder = {
    if (list.nonEmpty){
      val persisterTasks =  calctasks(list.size, maxNodesPerTask)
      val persisterBolt = persister match{
        case x : FusekiNodeConf => new CloudFoundryNodePersisterFusekiBolt(x)
        case x : VirtuosoNodeConf => new CloudFoundryNodePersisterVirtuosoBolt(x)
      }
      val boltReaderName = "cloudfoundryReader"
      val boltPersisterName = "cloudfoundryPersister"
      val persisterDeclarer = builder.setBolt(boltPersisterName, persisterBolt, persisterTasks)
      for(cfn <- list){
        val name = boltReaderName + "_" + cfn.id
        builder.setBolt(name, new CloudFoundryNodeClientBolt(cfn, pollTime)).
          allGrouping(timerSpoutName)
        persisterDeclarer.shuffleGrouping(name)
      }
    }
    builder
  }
  private def calctasks(nodes : Int, arity : Int): Int = (math ceil (nodes.toFloat / arity)) toInt
}
