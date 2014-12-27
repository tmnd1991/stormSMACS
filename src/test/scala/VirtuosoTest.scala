/**
 * Created by tmnd91 on 24/12/14.
 */

import java.io.ByteArrayOutputStream
import java.net.URL

import it.unibo.ing.monit.model.{MonitMonitoringStatus, MonitStatus, MonitProcessInfo}
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeData
import it.unibo.ing.utils.DateUtils
import java.util.Date
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfConversion._
import org.scalatest.{Matchers, FlatSpec}
import virtuoso.jena.driver.{VirtuosoUpdateFactory, VirtGraph}
import com.hp.hpl.jena.rdf.model.Model
import scala.concurrent.duration.Duration

class VirtuosoTest  extends FlatSpec with Matchers{
  val graphName = "<http://stormsmacs/sample/" + DateUtils.format(new Date(),"yyyy-MM-dd_HH:mm:ss>")
  val node = CFNodeData(new URL("http://192.168.1.10"),
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
      None))
  val dataAsString = node.toRdf().rdfSerialization("N-TRIPLE")
  val str = "INSERT INTO GRAPH " + graphName + " { " + dataAsString + "}"
  println(str)
  val set = new VirtGraph ("jdbc:virtuoso://localhost:1111", "dba", "dba")
  val vur = VirtuosoUpdateFactory.create(str, set)
  vur.exec
}



