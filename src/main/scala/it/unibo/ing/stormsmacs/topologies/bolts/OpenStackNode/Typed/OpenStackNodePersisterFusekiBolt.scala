package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import java.net.{URI, URL}
import java.util.Date
import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.rdf._
import it.unibo.ing.utils._
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{OpenStackNodeConf, FusekiNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackSampleData
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackRdfFormats._
import org.eclipse.jetty.client.{ContentExchange, HttpClient}
import org.eclipse.jetty.io.ByteArrayBuffer
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource}
import storm.scala.dsl.{Logging, TypedBolt}

/**
 * Created by tmnd91 on 10/01/15.
 */
class OpenStackNodePersisterFusekiBolt(fusekiEndpoint: FusekiNodeConf)
  extends TypedBolt[(OpenStackNodeConf, Date, Resource, Sample), Nothing]
  with Logging {
  private var httpClient: HttpClient = _
  setup {
    httpClient = new HttpClient()
    httpClient.setConnectTimeout(1000)
    httpClient.setMaxRedirects(1)
    httpClient.start()
  }
  shutdown{
    if (httpClient.isStarted)
      httpClient.stop()
    httpClient = null
  }

  override def typedExecute(t: (OpenStackNodeConf, Date, Resource, Sample), st: Tuple): Unit = {
    try {
      val graphName = GraphNamer.graphName(t._2)
      val url = new URL(t._1.ceilometerUrl.getProtocol + "://" + t._1.ceilometerUrl.getHost)
      val data = OpenStackSampleData(url, t._3.resource_id, t._4)
      val model = data.toRdf
      writeToRDFStore(graphName, model)
      st.ack
    }
    catch {
      case e: Throwable => {
        logger.error(e.getMessage + "\n" + e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }

  private def writeToRDFStore(graphName: String, data: Model): Unit = {
    val dataAsString = data.rdfSerialization("N-TRIPLE")
    val str = s"INSERT DATA { GRAPH $graphName { $dataAsString } }"
    val exchange = new ContentExchange()
    exchange.setURI(new URI(fusekiEndpoint.url / "update"))
    exchange.setMethod("POST")
    exchange.setRequestContentType("application/sparql-update")
    exchange.setRequestContent(new ByteArrayBuffer(str))
    httpClient.send(exchange)
    val state = exchange.waitForDone()
    if ((exchange.getResponseStatus/100) != 2)
      throw new Exception(s"Cannot sparql update: ${exchange.getResponseStatus} -> ${exchange.getResponseContent}")
  }
}
