package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import java.net.URI
import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.rdfBindings.{CFNodeResource, CFNodeSample}
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfFormat._
import it.unibo.ing.utils._
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{CloudFoundryNodeConf, FusekiNodeConf}
import org.apache.http.HttpStatus
import org.eclipse.jetty.client.{ContentExchange, HttpClient}
import org.eclipse.jetty.io.ByteArrayBuffer
import storm.scala.dsl.{Logging, TypedBolt}

/**
 * Created by tmnd91 on 24/12/14.
 */
class CloudFoundryNodePersisterFusekiBolt(fusekiEndpoint : FusekiNodeConf)
  extends TypedBolt[(CloudFoundryNodeConf, Date, MonitInfo), Nothing]
  with Logging{
  private var persisted : Set[Int] = _
  private var httpClient: HttpClient = _
  setup {
    httpClient = new HttpClient()
    httpClient.setConnectTimeout(1000)
    httpClient.setMaxRedirects(1)
    httpClient.start()
    persisted = Set()
  }
  shutdown{
    if (httpClient.isStarted)
      httpClient.stop()
    httpClient = null
    persisted = null
  }

  override def typedExecute(t: (CloudFoundryNodeConf, Date, MonitInfo), st : Tuple): Unit = {
    try{
      val graphName = GraphNamer.graphName(t._2)
      val sampleData = CFNodeSample(t._1.url, t._3)
      writeToRDFStore(graphName, sampleData.toRdf)
      if (!(persisted contains t._3.resId)){
        val resourceData = CFNodeResource(t._1.url, t._3)
        writeToRDFStore(GraphNamer.resourcesGraphName, resourceData.toRdf)
        persisted += t._3.resId
      }
      st.ack
    }
    catch{
      case e : Throwable => {
        logger.error(e.getMessage + "\n" + e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }

  private def writeToRDFStore(graphName : String, data : Model) : Unit = {
    val dataAsString = data.rdfSerialization("N-TRIPLE")
    val str = "INSERT DATA { GRAPH " + graphName + " { " + dataAsString + "} }"
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
