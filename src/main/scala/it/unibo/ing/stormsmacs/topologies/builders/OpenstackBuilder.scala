package it.unibo.ing.stormsmacs.topologies.builders

import java.util.Date

import backtype.storm.topology.TopologyBuilder
import it.unibo.ing.stormsmacs.conf.{VirtuosoNodeConf, PersisterNodeConf, OpenStackNodeConf, FusekiNodeConf}
import it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed.{OpenStackNodePersisterVirtuosoBolt, OpenStackNodePersisterFusekiBolt, OpenStackNodeSampleBolt, OpenStackNodeClientBolt}
import it.unibo.ing.stormsmacs.topologies.spouts.Typed.TimerSpout
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
      val boltClientName = "openstackClientBolt"
      val boltMeterName = "openstackMeterBolt"
      val boltPersisterName = "openstackPersister"
      val sampleClient = new OpenStackNodeClientBolt(list.head)
      val meterBolt = new OpenStackNodeSampleBolt(pollTime)
      val meterBoltDeclarer = builder.setBolt(boltMeterName, meterBolt)
      for(osn <- list){
        val name = boltClientName + "_" + osn.id
        builder.setBolt(name, new OpenStackNodeClientBolt(osn)).allGrouping(timerSpoutName)
        meterBoltDeclarer.shuffleGrouping(name)
      }
      persisterNode match{
        case x : FusekiNodeConf => builder.setBolt(boltPersisterName, new OpenStackNodePersisterFusekiBolt(x),persisterTasks).
          shuffleGrouping(boltMeterName)
        case x : VirtuosoNodeConf => builder.setBolt(boltPersisterName, new OpenStackNodePersisterVirtuosoBolt(x),persisterTasks).
          shuffleGrouping(boltMeterName)
      }
    }
    builder
  }
  private def calctasks(nodes : Int, arity : Int): Int = (math ceil (nodes.toFloat / arity)) toInt
}
