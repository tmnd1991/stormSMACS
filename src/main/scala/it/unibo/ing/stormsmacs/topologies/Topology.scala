package it.unibo.ing.stormsmacs.topologies

import java.io.{File, FileNotFoundException}

import backtype.storm.command.list
import backtype.storm.tuple.Fields
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

      configureGenericNodes(builder, conf.fusekiNode, conf.genericNodeList, timerSpoutName)

      configureOpenstackNodes(builder, conf.fusekiNode, conf.openstackNodeList, timerSpoutName)

      configureCloudFoundryNodes(builder, conf.fusekiNode, conf.cloudfoundryNodeList, timerSpoutName)

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

  private def configureOpenstackNodes( builder : TopologyBuilder,
                                       fusekiNode : FusekiNodeConf,
                                       list: Seq[OpenStackNodeConf],
                                       timerSpoutName : String) : Unit = {
    val boltClientName = "openstackClientBolt"
    val boltMeterName = "openstackMeterBolt"
    val boltPersisterName = "openstackPersister"
    for(osn <- list)
      builder.setBolt(boltClientName, new OpenStackNodeClientBolt(osn)).allGrouping(timerSpoutName)
    builder.setBolt(boltMeterName, new OpenStackNodeMeterBolt()).fieldsGrouping(boltClientName, new Fields("NodeName"))
    builder.setBolt(boltPersisterName,new OpenStackNodePersisterBolt(fusekiNode)).shuffleGrouping(boltMeterName)
  }

  private def configureGenericNodes(builder : TopologyBuilder,
                                    fusekiNode : FusekiNodeConf,
                                    list: Seq[GenericNodeConf],
                                    timerSpoutName : String) : Unit = {
    val boltReaderName = "genericReaderBolt"
    for(gn <- list)
      builder.setBolt(boltReaderName, new GenericNodeClientBolt(gn)).allGrouping(timerSpoutName)
    val boltPersisterName = "genericPersister"
    builder.setBolt(boltPersisterName,new GenericNodePersisterBolt(fusekiNode)).shuffleGrouping(boltReaderName)
  }
  private def configureCloudFoundryNodes(builder : TopologyBuilder,
                                         fusekiNode : FusekiNodeConf,
                                         list: Seq[CloudFoundryNodeConf],
                                         timerSpoutName : String) : Unit = {
    val boltReaderName = "cloudfoundryReader"
    for(cfn <- list){
      builder.setBolt(boltReaderName, new CloudFoundryNodeClientBolt(cfn)).allGrouping(timerSpoutName)
      val boltPersisterName = "cloudfoundryPersister"
      builder.setBolt(boltPersisterName,new CloudFoundryNodePersisterBolt(fusekiNode)).shuffleGrouping(boltReaderName)
  }


  }
}