package it.unibo.ing.stormsmacs.topologies

import java.io.{File, FileNotFoundException}
import java.util.Date
import it.unibo.ing.stormsmacs.serializers._
import org.slf4j.LoggerFactory
import backtype.storm.tuple.Fields
import backtype.storm.{Config, StormSubmitter, LocalCluster}
import storm.scala.dsl.{TypedTopologyBuilder, StormConfig}
import it.unibo.ing.stormsmacs.topologies.spouts.Typed.TimerSpout
import it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed.{CloudFoundryNodePersisterVirtuosoBolt, CloudFoundryNodePersisterFusekiBolt, CloudFoundryNodeClientBolt, CloudFoundryNodePersisterBolt}
import it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed.{GenericNodePersisterFusekiBolt, GenericNodePersisterVirtuosoBolt, GenericNodeClientBolt}
import it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed.{OpenStackNodePersisterFusekiBolt, OpenStackNodePersisterVirtuosoBolt, OpenStackNodeMeterBolt, OpenStackNodeClientBolt}
import it.unibo.ing.stormsmacs.conf._
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource, Statistics, Meter}
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.monit.model.{MonitSystemInfo, MonitProcessInfo, MonitInfo}
import scala.language.postfixOps


/**
 * Main configurable Topology of StormSMACS
 * @author Antonio Murgia
 * @version 18/11/14
 */
object Topology {
  val logger = LoggerFactory.getLogger(this.getClass)
  val arityOfPersister = 3
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
      conf.persisterNode match{
        case x : VirtuosoNodeConf =>{
          configureOpenstackNodes(conf.pollTime, builder, x, conf.openstackNodeList, timerSpout, timerSpoutName)
          configureGenericNodes(builder, x, conf.genericNodeList, timerSpout, timerSpoutName)
          configureCloudFoundryNodes(builder, x, conf.cloudfoundryNodeList, timerSpout, timerSpoutName)
        }
        case x : FusekiNodeConf =>{
          configureOpenstackNodes(conf.pollTime, builder, x, conf.openstackNodeList, timerSpout, timerSpoutName)
          configureGenericNodes(builder, x, conf.genericNodeList, timerSpout, timerSpoutName)
          configureCloudFoundryNodes(builder, x, conf.cloudfoundryNodeList, timerSpout, timerSpoutName)
        }
      }

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
      val persisterTasks = calctasks(list.size, arityOfPersister)
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
      builder.setBolt[(OpenStackNodeConf, Date, Resource, Sample)](boltMeterName, meterBolt,
        boltPersisterName, new OpenStackNodePersisterFusekiBolt(fusekiNode),persisterTasks).
        shuffleGrouping(boltMeterName)
    }
  }

  private def configureOpenstackNodes( pollTime : Long,
                                       builder : TypedTopologyBuilder,
                                       virtuosoNode : VirtuosoNodeConf,
                                       list: Seq[OpenStackNodeConf],
                                       timerSpout : TimerSpout,
                                       timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val persisterTasks = calctasks(list.size, arityOfPersister)
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
      builder.setBolt[(OpenStackNodeConf, Date, Resource, Sample)](boltMeterName, meterBolt,
                                                                    boltPersisterName, new OpenStackNodePersisterVirtuosoBolt(virtuosoNode),persisterTasks).
        shuffleGrouping(boltMeterName)
    }
  }

  private def configureGenericNodes(builder : TypedTopologyBuilder,
                                    fusekiNode : FusekiNodeConf,
                                    list: Seq[GenericNodeConf],
                                    timerSpout : TimerSpout,
                                    timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val persisterTasks =  calctasks(list.size, arityOfPersister)
      val boltReaderName = "genericReaderBolt"
      val boltPersisterName = "genericPersister"
      val sampleClient = new GenericNodeClientBolt(list.head)
      val persisterBolt = new GenericNodePersisterFusekiBolt(fusekiNode)
      val persisterDeclarer = builder.setBolt[(GenericNodeConf, Date, SigarMeteredData)](boltReaderName, sampleClient,
        boltPersisterName,persisterBolt,persisterTasks)
      for(gn <- list){
        val name = boltReaderName + "_" + gn.id
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
          name, new GenericNodeClientBolt(gn)).allGrouping(timerSpoutName)
        persisterDeclarer.shuffleGrouping(name)
      }
    }
  }

  private def configureGenericNodes(builder : TypedTopologyBuilder,
                                    virtuosoNodeConf : VirtuosoNodeConf,
                                    list: Seq[GenericNodeConf],
                                    timerSpout : TimerSpout,
                                    timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val persisterTasks =  calctasks(list.size, arityOfPersister)
      val boltReaderName = "genericReaderBolt"
      val boltPersisterName = "genericPersister"
      val sampleClient = new GenericNodeClientBolt(list.head)
      val persisterBolt = new GenericNodePersisterVirtuosoBolt(virtuosoNodeConf)
      val persisterDeclarer = builder.setBolt[(GenericNodeConf, Date, SigarMeteredData)](boltReaderName, sampleClient,
        boltPersisterName,persisterBolt,persisterTasks)
      for(gn <- list){
        val name = boltReaderName + "_" + gn.id
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
                                      name, new GenericNodeClientBolt(gn)).allGrouping(timerSpoutName)
        persisterDeclarer.shuffleGrouping(name)
      }
    }
  }

  private def configureCloudFoundryNodes(builder : TypedTopologyBuilder,
                                         fusekiNode : FusekiNodeConf,
                                         list: Seq[CloudFoundryNodeConf],
                                         timerSpout : TimerSpout,
                                         timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val persisterTasks =  calctasks(list.size, arityOfPersister)
      val boltReaderName = "cloudfoundryReader"
      val boltPersisterName = "cloudfoundryPersister"
      val sampleClient = new CloudFoundryNodeClientBolt(list.head)
      val persisterDeclarer = builder.setBolt[(CloudFoundryNodeConf, Date, MonitInfo)](boltReaderName, sampleClient,
        boltPersisterName,new CloudFoundryNodePersisterFusekiBolt(fusekiNode), persisterTasks)
      for(cfn <- list){
        val name = boltReaderName + "_" + cfn.id
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
                                      name, new CloudFoundryNodeClientBolt(cfn)).allGrouping(timerSpoutName)
        persisterDeclarer.shuffleGrouping(name)
      }
    }
  }
  private def configureCloudFoundryNodes(builder : TypedTopologyBuilder,
                                         virtuosoNode : VirtuosoNodeConf,
                                         list: Seq[CloudFoundryNodeConf],
                                         timerSpout : TimerSpout,
                                         timerSpoutName : String) : Unit = {
    if (list.nonEmpty){
      val persisterTasks =  calctasks(list.size, arityOfPersister)
      val boltReaderName = "cloudfoundryReader"
      val boltPersisterName = "cloudfoundryPersister"
      val sampleClient = new CloudFoundryNodeClientBolt(list.head)
      val persisterDeclarer = builder.setBolt[(CloudFoundryNodeConf, Date, MonitInfo)](boltReaderName, sampleClient,
        boltPersisterName,new CloudFoundryNodePersisterVirtuosoBolt(virtuosoNode), persisterTasks)
      for(cfn <- list){
        val name = boltReaderName + "_" + cfn.id
        builder.setBolt[Tuple1[Date]](timerSpoutName, timerSpout,
          name, new CloudFoundryNodeClientBolt(cfn)).allGrouping(timerSpoutName)
        persisterDeclarer.shuffleGrouping(name)
      }
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
  private def calctasks(nodes : Int, arity : Int): Int = (math ceil (nodes.toFloat / arity)) toInt
}
