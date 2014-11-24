package it.unibo.ing.stormsmacs.topologies

import java.io.{FileNotFoundException, File}

import backtype.storm.LocalCluster
import backtype.storm.topology.TopologyBuilder
import it.unibo.ing.stormsmacs.topologies.bolts.PrintBolt
import it.unibo.ing.stormsmacs.topologies.spouts.TimerSpout
import org.slf4j.LoggerFactory
import storm.scala.dsl.StormConfig

/**
 * Created by tmnd on 24/11/14.
 */
object DebugTopology {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]) = {

    val builder = new TopologyBuilder()
    builder.setSpout("timer", new TimerSpout(1000))
    builder.setBolt("print" ,new PrintBolt(), 10).allGrouping("timer")
    val sConf = new StormConfig(debug = true)
    val cluster = new LocalCluster()
    cluster.submitTopology("test", sConf, builder.createTopology())
  }
}
