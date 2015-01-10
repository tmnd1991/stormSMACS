import java.net.URL
import java.util.Date

import it.unibo.ing.monit.model.{MonitMonitoringStatus, MonitStatus, MonitProcessInfo}
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeData
import it.unibo.ing.utils._
import org.apache.commons.codec.binary.Base64
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.util.StringContentProvider
import org.scalatest.{Matchers, FlatSpec}
import virtuoso.jena.driver.{VirtuosoUpdateFactory, VirtGraph}
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfConversion._

import scala.concurrent.duration.Duration

/**
 * Created by tmnd91 on 03/01/15.
 */
class FusekiTest extends FlatSpec with Matchers{
  "we " should " be able to insert" in{
    val httpClient = new HttpClient()
    httpClient.setConnectTimeout(1000)
    httpClient.setFollowRedirects(false)
    httpClient.setStopTimeout(1000)
    httpClient.start()
    val d = new Date()
    val graphName = GraphNamer.graphName(d)
    val dataAsString = "<http://137.204.57.150/image> <http://ing.unibo.it/smacs/predicates#maxValue> \"1.0\" .\n<http://137.204.57.150/image> <http://ing.unibo.it/smacs/predicates#sumValue> \"486.0\" .\n<http://137.204.57.150/image> <http://ing.unibo.it/smacs/predicates#averageValue> \"1.0\" .\n<http://137.204.57.150/image> <http://ing.unibo.it/smacs/predicates#minValue> \"1.0\" .\n<http://137.204.57.150/image> <http://ing.unibo.it/smacs/predicates#period> \"0\" .\n<http://137.204.57.150/image> <http://ing.unibo.it/smacs/predicates#periodStart> \"Sat Jan 03 00:45:41 CET 2015\" .\n<http://137.204.57.150/image> <http://ing.unibo.it/smacs/predicates#periodEnd> \"Sat Jan 03 09:35:45 CET 2015\" .\n<http://137.204.57.150/image> <http://ing.unibo.it/smacs/predicates#duration> \"31804.0\" .\n<http://137.204.57.150/image> <http://ing.unibo.it/smacs/predicates#countValue> \"486\" .\n<http://137.204.57.150/image> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> \"OpenStack Node Statistics\" ."
    val str = "INSERT DATA { GRAPH " + graphName + " { " + dataAsString + "} }"
    val resp = httpClient.POST("http://localhost:3030/ds/update").header("Content-Type", "application/sparql-update").content(new StringContentProvider(str)).send()
    (resp.getStatus / 100) should be (2)
  }

  "virtuoso " should " work " in{
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
    val str = "INSERT DATA { GRAPH " + graphName + " { " + dataAsString + "} }"
    val set = new VirtGraph ("jdbc:virtuoso://localhost:1111", "dba", "dba")
    val vur = VirtuosoUpdateFactory.create(str, set)
    vur.exec
    true should be (true)
  }
}
