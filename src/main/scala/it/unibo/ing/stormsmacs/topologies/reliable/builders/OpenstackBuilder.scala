package it.unibo.ing.stormsmacs.topologies.reliable.builders

import java.util.Date

import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.{Fields, Values}
import it.unibo.ing.stormsmacs.conf.{VirtuosoNodeConf, PersisterNodeConf, OpenStackNodeConf, FusekiNodeConf}
import it.unibo.ing.stormsmacs.topologies.facilities.StormSmacsBuilder
import it.unibo.ing.stormsmacs.topologies.reliable.bolts.OpenStack.{OpenStackClientBolt, OpenStackPersisterFusekiBolt, OpenStackPersisterVirtuosoBolt, OpenStackSampleBolt}
import it.unibo.ing.stormsmacs.topologies.reliable.spouts.TimerSpout
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource}
import org.openstack.clients.ceilometer.v2.CeilometerClient
import scala.language.postfixOps

/**
 * @author Antonio Murgia
 * @version 08/03/15
 * Adds to the current topology all the spouts and bolts needed to monitor the list of Openstack node passed
 */
class OpenstackBuilder(pollTime : Long,
                       persisterNode : PersisterNodeConf,
                       list: Seq[OpenStackNodeConf],
                       timerSpoutName : String,
                       maxNodesPerTask : Int = 7) extends StormSmacsBuilder{
  override def build(builder: TopologyBuilder): TopologyBuilder = {
    if (list.nonEmpty){
      var nResources = 0
      for(osn <- list) {
        val cclient = CeilometerClient.getInstance(osn.ceilometerUrl, osn.keystoneUrl, osn.tenantName, osn.username, osn.password, 60000, 60000)
        cclient.tryListResources(Seq()) match {
          case Some(x: Seq[Resource]) => nResources += x.size
          case None => nResources += 0
        }
      }
      val tasks = calctasks(nResources,maxNodesPerTask)
      val boltClientName = "openstackResourcesClient"
      val boltSamplesName = "openstackSamplesClient"
      val boltPersisterName = "openstackPersister"
      val sampleBolt = new OpenStackSampleBolt(pollTime)
      val sampleBoltDeclarer = builder.setBolt(boltSamplesName, sampleBolt, tasks)
      for (osn <- list){
        val name = boltClientName + " [" + osn.id + "] "
        builder.setBolt(name, new OpenStackClientBolt(osn)).allGrouping(timerSpoutName)
        sampleBoltDeclarer.fieldsGrouping(name, new Fields("Resource"))
      }

      persisterNode match{
        case x : FusekiNodeConf => builder.setBolt(boltPersisterName, new OpenStackPersisterFusekiBolt(x),tasks).
          shuffleGrouping(boltSamplesName)
        case x : VirtuosoNodeConf => builder.setBolt(boltPersisterName, new OpenStackPersisterVirtuosoBolt(x),tasks).
          shuffleGrouping(boltSamplesName)
      }
    }
    builder
  }
  private def calctasks(nodes : Int, arity : Int): Int = (math ceil (nodes.toFloat / arity)) toInt
}
