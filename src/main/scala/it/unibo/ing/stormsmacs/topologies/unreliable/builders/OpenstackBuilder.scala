package it.unibo.ing.stormsmacs.topologies.unreliable.builders

import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.Fields
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, OpenStackNodeConf, PersisterNodeConf, VirtuosoNodeConf}
import it.unibo.ing.stormsmacs.topologies.facilities.StormSmacsBuilder
import it.unibo.ing.stormsmacs.topologies.unreliable.bolts.OpenStack.{OpenStackPersisterVirtuosoBolt, OpenStackPersisterFusekiBolt, OpenStackClientBolt, OpenStackSampleBolt}
import it.unibo.ing.stormsmacs.topologies.unreliable.bolts.OpenStack.{OpenStackClientBolt, OpenStackPersisterFusekiBolt, OpenStackPersisterVirtuosoBolt, OpenStackSampleBolt}
import it.unibo.ing.stormsmacs.topologies.unreliable.spouts.TimerSpout
import org.openstack.api.restful.ceilometer.v2.elements.Resource
import org.openstack.clients.ceilometer.v2.CeilometerClient

import scala.language.postfixOps
import scala.util.{Success, Failure}

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
                       maxNodesPerTask : Int = 20) extends StormSmacsBuilder{
  override def build(builder: TopologyBuilder): TopologyBuilder = {
    if (list.nonEmpty){
      var nResources = 0
      for(osn <- list) {
        val cclient = CeilometerClient.getInstance(osn.ceilometerUrl, osn.keystoneUrl, osn.tenantName, osn.username, osn.password, 60000, 60000)
        cclient.tryListAllResources match {
          case Failure(e) => nResources += 0
          case Success(Nil) => nResources += 0
          case Success(x: Seq[Resource]) => nResources += x.size
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
