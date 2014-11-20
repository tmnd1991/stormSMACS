package it.unibo.ing.stormsmacs.topologies

import java.io.{File, FileNotFoundException}

import it.unibo.ing.stormsmacs.topologies.bolts.{OpenStackNodePersisterBolt, GenericNodePersisterBolt, CloudFoundryNodePersisterBolt}
import it.unibo.ing.stormsmacs.topologies.spouts.{OpenStackNodeSpout, GenericNodeSpout, CloudFoundryNodeSpout}
import org.slf4j.LoggerFactory

import backtype.storm.LocalCluster
import backtype.storm.topology.TopologyBuilder

import storm.scala.dsl.StormConfig

import it.unibo.ing.stormsmacs.conf._

/**
 * Created by tmnd on 18/11/14.
 */
object Topology {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]) = {
    require(args.length == 1)
    val jsonConfFile = args(0)
    try{
      val jsonText = io.Source.fromFile(new File(jsonConfFile)).mkString
      val conf = JsonConfiguration.readJsonConf(jsonText)
      val builder = new TopologyBuilder()

      logger.info("starting stormsmacs with conf : \n" + conf)

      for(gn <- conf.genericNodeList){
        builder.setSpout(gn.id, new GenericNodeSpout(gn,conf.pollTime))
        builder.setBolt("generic_" + gn.id,new GenericNodePersisterBolt(conf.fusekiNode)).shuffleGrouping(gn.id)
      }

      for(osn <- conf.openstackNodeList){
        builder.setSpout(osn.id, new OpenStackNodeSpout(osn,conf.pollTime))
        builder.setBolt("openstack_" + osn.id,new OpenStackNodePersisterBolt(conf.fusekiNode)).shuffleGrouping(osn.id)
      }

      for(cfn <- conf.cloudfoundryNodeList){
        builder.setSpout(cfn.id, new CloudFoundryNodeSpout(cfn, conf.pollTime))
        builder.setBolt("cloudfoundry_" + cfn.id,new CloudFoundryNodePersisterBolt(conf.fusekiNode)).shuffleGrouping(cfn.id)
      }

      val sConf = new StormConfig(debug = conf.debug)
      val cluster = new LocalCluster()
      cluster.submitTopology(conf.name, sConf, builder.createTopology())
    }
    catch{
      case e : FileNotFoundException => logger.error("file " + jsonConfFile + " not Found")
      case t : Throwable => logger.error(t.getMessage)
    }
  }
}
