package it.unibo.ing.stormsmacs.topologies

import java.io.{File, FileNotFoundException}
import java.util.Date
import java.net.URL
import it.unibo.ing.stormsmacs.topologies.bolts.Debug.{osWriteToFileBolt, genWriteToFileBolt, cfWriteToFileBolt}
import org.slf4j.LoggerFactory
import backtype.storm.tuple.Fields
import backtype.storm.{StormSubmitter, LocalCluster}
import storm.scala.dsl.{TypedTopologyBuilder, StormConfig}
import it.unibo.ing.stormsmacs.topologies.spouts.Typed.TimerSpout
import it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed.{CloudFoundryNodePersisterFusekiBolt, CloudFoundryNodeClientBolt, CloudFoundryNodePersisterBolt}
import it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed.{GenericNodePersisterFusekiBolt, GenericNodePersisterVirtuosoBolt, GenericNodeClientBolt}
import it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed.{OpenStackNodePersisterFusekiBolt, OpenStackNodePersisterVirtuosoBolt, OpenStackNodeMeterBolt, OpenStackNodeClientBolt}
import it.unibo.ing.stormsmacs.conf._
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource, Statistics, Meter}
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.monit.model.MonitInfo

/**
 * Main configurable Topology of StormSMACS
 * @author Antonio Murgia
 * @version 18/11/14
 */
object DebugTopology {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]) = {
    val conf = new JsonConfiguration(
      "debug",
      Some(List(OpenStackNodeConf("os", "ceilometer_project",
                                  new URL("http://137.204.57.150:8777"),
                                  new URL("http://137.204.57.150:5000"),
                                  "amurgia","PUs3dAs?",
                                  Some(30000), Some(30000)))),
      Some(List(CloudFoundryNodeConf("cd", new URL("http://localhost:9876"),Some(10000), Some(10000)))),
      Some(List(GenericNodeConf("gn", new URL("http://localhost:9875"), Some(10000), Some(10000)))),
      VirtuosoNodeConf("virtuoso", "jdbc:virtuoso://localhost:1111", "dba", "dba"),
      false,
      false,
      60000
    )


    val builder = new TypedTopologyBuilder()

    logger.info("starting stormsmacs with conf : \n" + conf)
    logger.info("setting spout")

    val timerSpoutName = "timer"
    val timerSpout = new TimerSpout(conf.pollTime)
    builder.setSpout[Tuple1[Date]](timerSpoutName, timerSpout)
    logger.info("spout set")

    configureOpenstackNodes(builder, conf.persisterNode, conf.openstackNodeList, timerSpout, timerSpoutName)
    logger.info("configured openstack")

    configureGenericNodes(builder, conf.persisterNode, conf.genericNodeList, timerSpout, timerSpoutName)
    logger.info("configured generics")

    configureCloudFoundryNodes(builder, conf.persisterNode, conf.cloudfoundryNodeList, timerSpout, timerSpoutName)
    logger.info("configured cfs")

    logger.info("configured topology")

    val sConf = new StormConfig(debug = conf.debug)
    if (conf.remote){
      StormSubmitter.submitTopology(conf.name, sConf, builder.createTopology())
    }
    else{
      new LocalCluster().submitTopology(conf.name, sConf, builder.createTopology())
    }
  }

  private def configureOpenstackNodes( builder : TypedTopologyBuilder,
                                       persisterNode : PersisterNodeConf,
                                       list: Seq[OpenStackNodeConf],
                                       timerSpout : TimerSpout,
                                       timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val boltClientName = "openstackClientBolt"
      val boltMeterName = "openstackMeterBolt"
      val boltPersisterName = "openstackPersister"
      val persisterBolt = new OpenStackNodePersisterFusekiBolt(FusekiNodeConf("fuseki", "http://localhost:3030/ds"))
      val sampleClient = new OpenStackNodeClientBolt(list.head)
      for(osn <- list)
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
          boltClientName, new OpenStackNodeClientBolt(osn)).allGrouping(timerSpoutName)
      val meterBolt = new OpenStackNodeMeterBolt(60000)
      builder.setBolt[(OpenStackNodeConf, Date, Resource)](boltClientName, sampleClient,
        boltMeterName, meterBolt,3).shuffleGrouping(boltClientName)
      builder.setBolt[(OpenStackNodeConf, Date, Resource, Sample)](boltMeterName, meterBolt,
        boltPersisterName, persisterBolt).
        shuffleGrouping(boltMeterName)
    }
  }

  private def configureGenericNodes(builder : TypedTopologyBuilder,
                                    persisterNode : PersisterNodeConf,
                                    list: Seq[GenericNodeConf],
                                    timerSpout : TimerSpout,
                                    timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val boltReaderName = "genericReaderBolt"
      val boltPersisterName = "genericPersister"
      val sampleClient = new GenericNodeClientBolt(list.head)
      for(gn <- list)
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
          boltReaderName, new GenericNodeClientBolt(gn),3).allGrouping(timerSpoutName)
      val persisterBolt = new GenericNodePersisterFusekiBolt(FusekiNodeConf("fuseki", "http://localhost:3030/ds"))
      builder.setBolt[(GenericNodeConf, Date, SigarMeteredData)](boltReaderName, sampleClient,
        boltPersisterName,persisterBolt).
        shuffleGrouping(boltReaderName)
    }
  }

  private def configureCloudFoundryNodes(builder : TypedTopologyBuilder,
                                         persisterNode : PersisterNodeConf,
                                         list: Seq[CloudFoundryNodeConf],
                                         timerSpout : TimerSpout,
                                         timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val boltReaderName = "cloudfoundryReader"
      val boltPersisterName = "cloudfoundryPersister"
      val sampleClient = new CloudFoundryNodeClientBolt(list.head)
      val persisterBolt = new CloudFoundryNodePersisterFusekiBolt(FusekiNodeConf("fuseki", "http://localhost:3030/ds"))
      for(cfn <- list)
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
          boltReaderName, new CloudFoundryNodeClientBolt(cfn),3).allGrouping(timerSpoutName)
      builder.setBolt[(CloudFoundryNodeConf, Date, MonitInfo)](boltReaderName, sampleClient,
        boltPersisterName,persisterBolt,3).
        shuffleGrouping(boltReaderName)
    }
  }

  def readConfFromJsonFile(filename : String) = {
    val jsonText = io.Source.fromFile(new File(filename)).mkString
    JsonConfiguration.readJsonConf(jsonText)
  }
}