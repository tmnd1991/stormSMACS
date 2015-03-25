package it.unibo.ing.stormsmacs.topologies

import java.io.{File, FileNotFoundException}

import backtype.storm.{Config, LocalCluster, StormSubmitter}
import com.esotericsoftware.kryo.serializers.DefaultSerializers.DateSerializer
import it.unibo.ing.monit.model.{MonitProcessInfo, MonitSystemInfo}
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf._
import it.unibo.ing.stormsmacs.serializers._
import it.unibo.ing.stormsmacs.topologies.builders.{CloudFoundryBuilder, GenericBuilder, OpenstackBuilder}
import it.unibo.ing.stormsmacs.topologies.spouts.Typed.TimerSpout
import org.openstack.api.restful.ceilometer.v2.elements.{Meter, Statistics}
import org.slf4j.LoggerFactory
import storm.scala.dsl.{StormConfig, TypedTopologyBuilder}

import scala.language.postfixOps


/**
 * Main configurable Topology of StormSMACS
 * @author Antonio Murgia
 * @version 18/11/14
 */
object Topology {
  val logger = LoggerFactory.getLogger(this.getClass)
  def main(args: Array[String]) = {
    val maxNodesPerTask = 3
    require(args.length == 1)
    val jsonConfFile = args(0)
    try{
      val conf = readConfFromJsonFile(jsonConfFile)
      val builder = new TypedTopologyBuilder()

      logger.info("starting stormsmacs with conf : \n" + conf)

      val timerSpoutName = "timer"
      val timerSpout = new TimerSpout(conf.pollTime)
      builder.setSpout(timerSpoutName, timerSpout)


      (new OpenstackBuilder(conf.pollTime, conf.persisterNode, conf.openstackNodeList, timerSpout, timerSpoutName, maxNodesPerTask)).build(builder)
      (new GenericBuilder(conf.persisterNode, conf.genericNodeList, timerSpout, timerSpoutName, maxNodesPerTask)).build(builder)
      (new CloudFoundryBuilder(conf.persisterNode, conf.cloudfoundryNodeList, timerSpout, timerSpoutName, maxNodesPerTask)).build(builder)

      val sConf = new StormConfig(debug = conf.debug)
      registerSerializers(sConf)
      if (conf.remote){
        sConf.setNumWorkers((math ceil (conf.nodesNumber.toFloat / 20)) toInt)
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
    conf.setFallBackOnJavaSerialization(false)
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
    conf.registerSerialization(classOf[java.util.Date], classOf[DateSerializer])
  }
}