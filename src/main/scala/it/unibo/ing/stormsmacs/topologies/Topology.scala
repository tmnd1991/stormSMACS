package it.unibo.ing.stormsmacs.topologies

import java.io.{File, FileNotFoundException}

import backtype.storm.topology.TopologyBuilder
import backtype.storm.{Config, LocalCluster, StormSubmitter}
import com.esotericsoftware.kryo.serializers.DefaultSerializers.DateSerializer
import it.unibo.ing.monit.model.{MonitInfo, MonitProcessInfo, MonitSystemInfo}
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf._
import it.unibo.ing.stormsmacs.serializers._
import it.unibo.ing.stormsmacs.topologies.builders.{CloudFoundryBuilder, GenericBuilder, OpenstackBuilder}
import it.unibo.ing.stormsmacs.topologies.spouts.Typed.TimerSpout
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource, Meter, Statistics}
import org.openstack.api.restful.elements.Link
import org.slf4j.LoggerFactory

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
      val builder = new TopologyBuilder()

      logger.info("starting stormsmacs with conf : \n" + conf)

      val timerSpoutName = "timer"
      val timerSpout = new TimerSpout(conf.pollTime)
      builder.setSpout(timerSpoutName, timerSpout)


      (new OpenstackBuilder(conf.pollTime, conf.persisterNode, conf.openstackNodeList, timerSpout, timerSpoutName, maxNodesPerTask)).build(builder)
      (new GenericBuilder(conf.persisterNode, conf.genericNodeList, timerSpout, timerSpoutName, maxNodesPerTask)).build(builder)
      (new CloudFoundryBuilder(conf.persisterNode, conf.cloudfoundryNodeList, timerSpout, timerSpoutName, maxNodesPerTask)).build(builder)

      val config = new Config()
      config.setDebug(conf.debug)
      registerSerializers(config)
      if (conf.remote){
        config.setNumWorkers((math ceil (conf.nodesNumber.toFloat / 50)) toInt)
        StormSubmitter.submitTopology(conf.name, config, builder.createTopology())
      }
      else{
        new LocalCluster().submitTopology(conf.name, config, builder.createTopology())
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
    conf.setFallBackOnJavaSerialization(true)
    conf.registerSerialization(classOf[Link], classOf[LinkSerializer])
    conf.registerSerialization(classOf[java.sql.Timestamp], classOf[com.twitter.chill.java.TimestampSerializer])
    conf.registerSerialization(classOf[CloudFoundryNodeConf], classOf[CloudFoundryNodeConfSerializer])
    conf.registerSerialization(classOf[GenericNodeConf], classOf[GenericNodeConfSerializer])
    conf.registerSerialization(classOf[MonitProcessInfo], classOf[MonitProcessInfoSerializer])
    conf.registerSerialization(classOf[MonitSystemInfo], classOf[MonitSystemInfoSerializer])
    conf.registerSerialization(classOf[OpenStackNodeConf], classOf[OpenStackNodeConfSerializer])
    conf.registerSerialization(classOf[SigarMeteredData], classOf[SigarMeteredDataSerializer])
    conf.registerSerialization(classOf[PersisterNodeConf], classOf[PersisterNodeConfSerializer])
    conf.registerSerialization(classOf[FusekiNodeConf], classOf[PersisterNodeConfSerializer])
    conf.registerSerialization(classOf[VirtuosoNodeConf], classOf[PersisterNodeConfSerializer])
    conf.registerSerialization(classOf[java.util.Date], classOf[DateSerializer])
    conf.registerSerialization(classOf[Resource], classOf[ResourceSerializer])
    conf.registerSerialization(classOf[Sample], classOf[SampleSerializer])
  }
}