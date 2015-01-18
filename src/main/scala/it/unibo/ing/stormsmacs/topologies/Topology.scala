package it.unibo.ing.stormsmacs.topologies

import java.io.{File, FileNotFoundException}
import java.util.Date
import it.unibo.ing.stormsmacs.serializers._
import org.slf4j.LoggerFactory
import backtype.storm.tuple.Fields
import backtype.storm.{Config, StormSubmitter, LocalCluster}
import storm.scala.dsl.{TypedTopologyBuilder, StormConfig}
import it.unibo.ing.stormsmacs.topologies.spouts.Typed.TimerSpout
import it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed.{CloudFoundryNodeClientBolt, CloudFoundryNodePersisterBolt}
import it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed.{GenericNodePersisterBolt, GenericNodeClientBolt}
import it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed.{OpenStackNodePersisterBolt, OpenStackNodeMeterBolt, OpenStackNodeClientBolt}
import it.unibo.ing.stormsmacs.conf._
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource, Statistics, Meter}
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.monit.model.{MonitSystemInfo, MonitProcessInfo, MonitInfo}


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
      val conf = readConfFromJsonFile(jsonConfFile)
      val builder = new TypedTopologyBuilder()

      logger.info("starting stormsmacs with conf : \n" + conf)

      val timerSpoutName = "timer"
      val timerSpout = new TimerSpout(conf.pollTime)
      builder.setSpout(timerSpoutName, timerSpout)

      configureOpenstackNodes(conf.pollTime, builder, conf.fusekiNode, conf.openstackNodeList, timerSpout, timerSpoutName)

      configureGenericNodes(builder, conf.fusekiNode, conf.genericNodeList, timerSpout, timerSpoutName)

      configureCloudFoundryNodes(builder, conf.fusekiNode, conf.cloudfoundryNodeList, timerSpout, timerSpoutName)

      val sConf = new StormConfig(debug = conf.debug)
      registerSerializers(sConf)
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

  private def configureOpenstackNodes( pollTime : Long,
                                       builder : TypedTopologyBuilder,
                                       fusekiNode : FusekiNodeConf,
                                       list: Seq[OpenStackNodeConf],
                                       timerSpout : TimerSpout,
                                       timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val boltClientName = "openstackClientBolt"
      val boltMeterName = "openstackMeterBolt"
      val boltPersisterName = "openstackPersister"
      val sampleClient = new OpenStackNodeClientBolt(list.head)
      for(osn <- list)
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
                                      boltClientName, new OpenStackNodeClientBolt(osn)).allGrouping(timerSpoutName)
      val meterBolt = new OpenStackNodeMeterBolt(pollTime)
      builder.setBolt[(OpenStackNodeConf, Date, Resource)](boltClientName, sampleClient,
                                                        boltMeterName, meterBolt).shuffleGrouping(boltClientName)
      builder.setBolt[(OpenStackNodeConf, Date, Resource, Sample)](boltMeterName, meterBolt,
                                                                    boltPersisterName, new OpenStackNodePersisterBolt(fusekiNode)).
        shuffleGrouping(boltMeterName)
    }
  }

  private def configureGenericNodes(builder : TypedTopologyBuilder,
                                    fusekiNode : FusekiNodeConf,
                                    list: Seq[GenericNodeConf],
                                    timerSpout : TimerSpout,
                                    timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val boltReaderName = "genericReaderBolt"
      val boltPersisterName = "genericPersister"
      val sampleClient = new GenericNodeClientBolt(list.head)
      for(gn <- list)
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
                                      boltReaderName, new GenericNodeClientBolt(gn)).allGrouping(timerSpoutName)
      val persisterBolt = new GenericNodePersisterBolt(fusekiNode)
      builder.setBolt[(GenericNodeConf, Date, SigarMeteredData)](boltReaderName, sampleClient,
                                                                boltPersisterName,persisterBolt).
        shuffleGrouping(boltReaderName)
    }
  }

  private def configureCloudFoundryNodes(builder : TypedTopologyBuilder,
                                         fusekiNode : FusekiNodeConf,
                                         list: Seq[CloudFoundryNodeConf],
                                         timerSpout : TimerSpout,
                                         timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val boltReaderName = "cloudfoundryReader"
      val boltPersisterName = "cloudfoundryPersister"
      val sampleClient = new CloudFoundryNodeClientBolt(list.head)
      for(cfn <- list)
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
                                      boltReaderName, new CloudFoundryNodeClientBolt(cfn)).allGrouping(timerSpoutName)
      builder.setBolt[(CloudFoundryNodeConf, Date, MonitInfo)](boltReaderName, sampleClient,
                                                               boltPersisterName,new CloudFoundryNodePersisterBolt(fusekiNode)).
        shuffleGrouping(boltReaderName)



    }
  }

  def readConfFromJsonFile(filename : String) = {
    val jsonText = io.Source.fromFile(new File(filename)).mkString
    JsonConfiguration.readJsonConf(jsonText)
  }
  private def registerSerializers(conf : Config) : Unit = {
    conf.registerSerialization(classOf[CloudFoundryNodeConf], classOf[CloudFoundryNodeConfSerializer])
    conf.registerSerialization(classOf[GenericNodeConf], classOf[GenericNodeConfSerializer])
    conf.registerSerialization(classOf[Meter], classOf[MeterSerializer])
    conf.registerSerialization(classOf[MonitProcessInfo], classOf[MonitProcessInfoSerializer])
    conf.registerSerialization(classOf[MonitSystemInfo], classOf[MonitSystemInfoSerializer])
    conf.registerSerialization(classOf[OpenStackNodeConf], classOf[OpenStackNodeConfSerializer])
    conf.registerSerialization(classOf[SigarMeteredData], classOf[SigarMeteredDataSerializer])
    conf.registerSerialization(classOf[Statistics], classOf[StatisticsSerializer])
  }
}
