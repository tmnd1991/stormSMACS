package it.unibo.ing.stormsmacs.topologies.unreliable.builders

import backtype.storm.topology.TopologyBuilder
import it.unibo.ing.stormsmacs.conf.{CloudFoundryNodeConf, FusekiNodeConf, PersisterNodeConf, VirtuosoNodeConf}
import it.unibo.ing.stormsmacs.topologies.facilities.StormSmacsBuilder
import it.unibo.ing.stormsmacs.topologies.unreliable.bolts.CloudFoundry.{CloudFoundryClientBolt, CloudFoundryPersisterFusekiBolt, CloudFoundryPersisterVirtuosoBolt}
import it.unibo.ing.stormsmacs.topologies.unreliable.spouts.TimerSpout

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
        case x : FusekiNodeConf => new CloudFoundryPersisterFusekiBolt(x)
        case x : VirtuosoNodeConf => new CloudFoundryPersisterVirtuosoBolt(x)
      }
      val boltReaderName = "cfReader"
      val boltPersisterName = "cfPersister"
      val persisterDeclarer = builder.setBolt(boltPersisterName, persisterBolt, persisterTasks)
      for(cfn <- list){
        val name = boltReaderName + " [" + cfn.id + "] "
        builder.setBolt(name, new CloudFoundryClientBolt(cfn, pollTime)).allGrouping(timerSpoutName)
        persisterDeclarer.shuffleGrouping(name)
      }
    }
    builder
  }
  private def calctasks(nodes : Int, arity : Int): Int = (math ceil (nodes.toFloat / arity)) toInt
}
