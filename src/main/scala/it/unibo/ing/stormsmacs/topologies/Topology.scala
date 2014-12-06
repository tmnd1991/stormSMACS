package it.unibo.ing.stormsmacs.topologies

import java.io.{File, FileNotFoundException}

import it.unibo.ing.stormsmacs.topologies.bolts._
import it.unibo.ing.stormsmacs.topologies.spouts.TimerSpout
import org.slf4j.LoggerFactory

import backtype.storm.{StormSubmitter, LocalCluster}
import backtype.storm.topology.TopologyBuilder

import storm.scala.dsl.StormConfig

import it.unibo.ing.stormsmacs.conf._

/**
 * Main configurable Topology of StormSMACS
 * @author Antonio Murgia
 * @version 18/11/14
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
      val timerSpoutName = "timer"
      builder.setSpout(timerSpoutName, new TimerSpout(conf.pollTime))
      for(gn <- conf.genericNodeList){
        val boltReaderName = "genericReader_" + gn.id
        builder.setBolt(boltReaderName, new GenericNodeClientBolt(gn)).allGrouping(timerSpoutName)
        val boltPersisterName = "genericPersister_" + gn.id
        builder.setBolt(boltPersisterName,new GenericNodePersisterBolt(conf.fusekiNode)).shuffleGrouping(boltReaderName)
      }

      for(osn <- conf.openstackNodeList){
        val boltReaderName = "openstackReader_" + osn.id
        builder.setBolt(boltReaderName, new OpenStackNodeClientBolt(osn)).allGrouping(timerSpoutName)
        val boltPersisterName = "openstackPersister_" + osn.id
        builder.setBolt(boltPersisterName,new OpenStackNodePersisterBolt(conf.fusekiNode)).shuffleGrouping(boltReaderName)
      }

      for(cfn <- conf.cloudfoundryNodeList){
        val boltReaderName = "cloudfoundryReader_" + cfn.id
        builder.setBolt(boltReaderName, new CloudFoundryNodeClientBolt(cfn)).allGrouping(timerSpoutName)
        val boltPersisterName = "cloudfoundryPersister_" + cfn.id
        builder.setBolt(boltPersisterName,new CloudFoundryNodePersisterBolt(conf.fusekiNode)).shuffleGrouping(boltReaderName)
      }

      val sConf = new StormConfig(debug = conf.debug)
      if (conf.remote){
        StormSubmitter.submitTopology(conf.name, sConf, builder.createTopology())
      }
      else{
        new LocalCluster().submitTopology(conf.name, sConf, builder.createTopology())
      }
    }
    catch{
      case e : FileNotFoundException => logger.error("file " + jsonConfFile + " not Found")
      case t : Throwable => logger.error(t.getMessage)
    }
  }
}