package it.unibo.ing.stormsmacs.topologies.builders

import java.util.Date

import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.{VirtuosoNodeConf, FusekiNodeConf, GenericNodeConf, PersisterNodeConf}
import it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed.{GenericNodePersisterFusekiBolt, GenericNodePersisterVirtuosoBolt, GenericNodeClientBolt}
import it.unibo.ing.stormsmacs.topologies.spouts.Typed.TimerSpout
import scala.language.postfixOps
import storm.scala.dsl.TypedTopologyBuilder

/**
 * @author Antonio Murgia
 * @version 08/03/15
 * Adds to the current topology all the spouts and bolts needed to monitor the list of generic node passed
 */
class GenericBuilder(persisterNode : PersisterNodeConf,
                     list: Seq[GenericNodeConf],
                     timerSpout : TimerSpout,
                     timerSpoutName : String,
                     maxNodesPerTask : Int = 3) extends StormSmacsBuilder{
  override def build(builder: TypedTopologyBuilder): TypedTopologyBuilder = {
    if (list.nonEmpty){
      val persisterTasks =  calctasks(list.size, maxNodesPerTask)
      val boltReaderName = "genericReaderBolt"
      val boltPersisterName = "genericPersister"
      val sampleClient = new GenericNodeClientBolt(list.head)
      val persisterBolt = persisterNode match{
        case x : FusekiNodeConf => new GenericNodePersisterFusekiBolt(x)
        case x : VirtuosoNodeConf => new GenericNodePersisterVirtuosoBolt(x)
      }
      val persisterDeclarer = builder.setBolt[(GenericNodeConf, Date, SigarMeteredData)](boltReaderName, sampleClient,
        boltPersisterName,persisterBolt,persisterTasks)
      for(gn <- list){
        val name = boltReaderName + "_" + gn.id
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
          name, new GenericNodeClientBolt(gn)).allGrouping(timerSpoutName)
        persisterDeclarer.shuffleGrouping(name)
      }
    }
    builder
  }
  private def calctasks(nodes : Int, arity : Int): Int = (math ceil (nodes.toFloat / arity)) toInt
}
