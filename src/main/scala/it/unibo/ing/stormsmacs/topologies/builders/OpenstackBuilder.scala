package it.unibo.ing.stormsmacs.topologies.builders

import java.util.Date

import backtype.storm.topology.TopologyBuilder
import it.unibo.ing.stormsmacs.conf.{VirtuosoNodeConf, PersisterNodeConf, OpenStackNodeConf, FusekiNodeConf}
import it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed.{OpenStackNodePersisterVirtuosoBolt, OpenStackNodePersisterFusekiBolt, OpenStackNodeMeterBolt, OpenStackNodeClientBolt}
import it.unibo.ing.stormsmacs.topologies.spouts.Typed.TimerSpout
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource}
import storm.scala.dsl.TypedTopologyBuilder

/**
 * Created by tmnd91 on 08/03/15.
 */
class OpenstackBuilder(pollTime : Long,
                       persisterNode : PersisterNodeConf,
                       list: Seq[OpenStackNodeConf],
                       timerSpout : TimerSpout,
                       timerSpoutName : String,
                       maxNodesPerTask : Int = 3) extends StormSmacsBuilder{
  override def build(builder: TypedTopologyBuilder): TypedTopologyBuilder = {
    if (list.nonEmpty){
      val persisterTasks = calctasks(list.size, maxNodesPerTask)
      val boltClientName = "openstackClientBolt"
      val boltMeterName = "openstackMeterBolt"
      val boltPersisterName = "openstackPersister"
      val sampleClient = new OpenStackNodeClientBolt(list.head)
      val meterBolt = new OpenStackNodeMeterBolt(pollTime)
      val meterBoltDeclarer = builder.setBolt[(OpenStackNodeConf, Date, Resource)](boltClientName, sampleClient,
        boltMeterName, meterBolt)
      for(osn <- list){
        val name = boltClientName + "_" + osn.id
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
          name, new OpenStackNodeClientBolt(osn)).allGrouping(timerSpoutName)
        meterBoltDeclarer.shuffleGrouping(name)
      }
      persisterNode match{
        case x : FusekiNodeConf => builder.setBolt[(OpenStackNodeConf, Date, Resource, Sample)](boltMeterName, meterBolt,
          boltPersisterName, new OpenStackNodePersisterFusekiBolt(x),persisterTasks).
          shuffleGrouping(boltMeterName)
        case x : VirtuosoNodeConf => builder.setBolt[(OpenStackNodeConf, Date, Resource, Sample)](boltMeterName, meterBolt,
          boltPersisterName, new OpenStackNodePersisterVirtuosoBolt(x),persisterTasks).
          shuffleGrouping(boltMeterName)
      }
    }
    builder
  }
  private def calctasks(nodes : Int, arity : Int): Int = (math ceil (nodes.toFloat / arity)) toInt
}
