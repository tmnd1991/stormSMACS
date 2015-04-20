package it.unibo.ing.stormsmacs.topologies.facilities

import java.net.URI

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.conf.FusekiNodeConf
import it.unibo.ing.utils._
import org.eclipse.jetty.client.{ContentExchange, HttpClient}
import org.eclipse.jetty.io.ByteArrayBuffer
/**
 * @author Antonio Murgia
 * @version 02/03/2015
 * Trait that adds the functionality to write to a Fuseki endpoint
 */
trait FusekiPersister {
  protected def writeToRDFStore(fusekiEndpoint : FusekiNodeConf, httpClient : HttpClient,
                                graphName: String, data: Model): Unit = {
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
