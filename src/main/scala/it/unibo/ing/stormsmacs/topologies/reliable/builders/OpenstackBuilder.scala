package it.unibo.ing.stormsmacs.topologies.reliable.builders

import java.util.Date

import backtype.storm.topology.TopologyBuilder
import it.unibo.ing.stormsmacs.conf.{VirtuosoNodeConf, PersisterNodeConf, OpenStackNodeConf, FusekiNodeConf}
import it.unibo.ing.stormsmacs.topologies.facilities.StormSmacsBuilder
import it.unibo.ing.stormsmacs.topologies.reliable.bolts.OpenStack.{OpenStackClientBolt, OpenStackPersisterFusekiBolt, OpenStackPersisterVirtuosoBolt, OpenStackSampleBolt}
import it.unibo.ing.stormsmacs.topologies.reliable.spouts.TimerSpout
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource}
import scala.language.postfixOps

/**
 * @author Antonio Murgia
 * @version 08/03/15
 * Adds to the current topology all the spouts and bolts needed to monitor the list of Openstack node passed
 */
class OpenstackBuilder(pollTime : Long,
                       persisterNode : PersisterNodeConf,
                       list: Seq[OpenStackNodeConf],
                       timerSpout : TimerSpout,
                       timerSpoutName : String,
                       maxNodesPerTask : Int = 3) extends StormSmacsBuilder{
  override def build(builder: TopologyBuilder): TopologyBuilder = {
    if (list.nonEmpty){
      val persisterTasks = calctasks(list.size, maxNodesPerTask)
      val boltClientName = "openstackResourcesClient"
      val boltSamplesName = "openstackSamplesClient"
      val boltPersisterName = "openstackPersister"
      val meterBolt = new OpenStackSampleBolt(pollTime)
      val meterBoltDeclarer = builder.setBolt(boltSamplesName, meterBolt, persisterTasks)
      for(osn <- list){
        val name = boltClientName + " [" + osn.id + "] "
        builder.setBolt(name, new OpenStackClientBolt(osn)).allGrouping(timerSpoutName)
        meterBoltDeclarer.shuffleGrouping(name)
      }
      persisterNode match{
        case x : FusekiNodeConf => builder.setBolt(boltPersisterName, new OpenStackPersisterFusekiBolt(x),persisterTasks).
          shuffleGrouping(boltSamplesName)
        case x : VirtuosoNodeConf => builder.setBolt(boltPersisterName, new OpenStackPersisterVirtuosoBolt(x),persisterTasks).
          shuffleGrouping(boltSamplesName)
      }
    }
    builder
  }
  private def calctasks(nodes : Int, arity : Int): Int = (math ceil (nodes.toFloat / arity)) toInt
}
