package it.unibo.ing.stormsmacs.topologies

import java.io.{File, FileNotFoundException}
import java.util.Date
import it.unibo.ing.stormsmacs.serializers._
import it.unibo.ing.stormsmacs.topologies.builders.{CloudFoundryBuilder, GenericBuilder, OpenstackBuilder}
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
      (new OpenstackBuilder(conf.pollTime, conf.persisterNode, conf.openstackNodeList, timerSpout, timerSpoutName)).build(builder)
      (new GenericBuilder(conf.persisterNode, conf.genericNodeList, timerSpout, timerSpoutName)).build(builder)
      (new CloudFoundryBuilder(conf.persisterNode, conf.cloudfoundryNodeList, timerSpout, timerSpoutName)).build(builder)

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
    conf.registerSerialization(classOf[PersisterNodeConf], classOf[PersisterNodeConfSerializer])
    conf.registerSerialization(classOf[FusekiNodeConf], classOf[FusekiNodeConfSerializer])
    conf.registerSerialization(classOf[VirtuosoNodeConf], classOf[VirtuosoNodeConfSerializer])
  }
}
