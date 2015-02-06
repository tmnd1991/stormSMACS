package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import java.net.URI
import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.rdf._
import it.unibo.ing.utils._
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, GenericNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeData
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeDataRdfFormat._
import org.eclipse.jetty.client.{ContentExchange, HttpClient}
import org.eclipse.jetty.io.ByteArrayBuffer
import storm.scala.dsl.{Logging, TypedBolt}

/**
 * Created by tmnd91 on 24/12/14.
 */
class GenericNodePersisterFusekiBolt(fusekiEndpoint : FusekiNodeConf)
  extends TypedBolt[(GenericNodeConf, Date, SigarMeteredData), Nothing]
  with Logging
{
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
  override def typedExecute(t: (GenericNodeConf, Date, SigarMeteredData), st : Tuple): Unit = {
    try{
      val graphName = GraphNamer.graphName(t._2)
      val data = GenericNodeData(t._1.url, t._3)
      val model = data.toRdf()
      writeToRDFStore(graphName, model)
      st.ack
    }
    catch{
      case e: Throwable => {
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
    if ((exchange.getStatus/100) != 2)
      throw new Exception(s"Cannot sparql update: ${exchange.getStatus} -> ${exchange.getResponseContent}")
  }
}