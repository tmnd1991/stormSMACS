/**
 * Created by tmnd on 25/11/14.
 */

import java.io.File
import java.util.Date

import it.unibo.ing.monit.model.{MonitResponseTime, MonitMonitoringStatus, MonitStatus, MonitProcessInfo}
import it.unibo.ing.stormsmacs.conf.JsonConfiguration
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeData
import java.net.URL
import org.scalatest._
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfConversion._

import scala.concurrent.duration.Duration

class MainTest extends FlatSpec with Matchers{
  /*
  val jsonConfFile = "/confExample.json"
  val jsonText = io.Source.fromFile(new File(jsonConfFile)).mkString
  val conf = JsonConfiguration.readJsonConf(jsonText)
  */
  val node = CFNodeData( new URL("http://192.168.1.10"),
  new MonitProcessInfo("pippo",
    MonitStatus.RUNNING,
    MonitMonitoringStatus.MONITORED,
    3,
    1,
    Duration("3s"),
    4,
    1000,
    2000,
    212.2f,
    120.2f,
    2.1f,
    2.2f,
    new Date(),
    None,
    None)
  )

  node.toRdf(node.url.toString).write(System.out)
  node.toRdf(node.url.toString).write(System.out, "N-TRIPLE")
}

/**
 * LOAD  INTO GRAPH <http://graphURI>
 */
