package it.unibo.ing.stormsmacs.topologies

import java.io.{FileNotFoundException, File}
import java.net.URL

import backtype.storm.LocalCluster
import backtype.storm.topology.TopologyBuilder
import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.{OpenStackNodeClientBolt, PrintBolt}
import it.unibo.ing.stormsmacs.topologies.spouts.TimerSpout
import org.slf4j.LoggerFactory
import storm.scala.dsl.StormConfig

/**
 * @author Antonio Murgia
 * @version 24/11/14
 */
object DebugTopology {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]) = {
    val builder = new TopologyBuilder()
    builder.setSpout("timer", new TimerSpout(1000))
    val keystoneURL = new URL("http://137.204.57.150:5000")
    val ceilometerURL = new URL("http://137.204.57.150:8777")
    val tenantName = "ceilometer_project"
    val username = "amurgia"
    val password = "PUs3dAs?"
    builder.setBolt("print" ,new OpenStackNodeClientBolt(
      OpenStackNodeConf.apply("bb",
        tenantName,
        ceilometerURL,
        keystoneURL,
        username,
        password,
        1000, None, None)
    ), 10).shuffleGrouping("timer")
    val sConf = new StormConfig(debug = true)
    val cluster = new LocalCluster()
    cluster.submitTopology("test", sConf, builder.createTopology())
  }
}
