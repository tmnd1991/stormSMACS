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
import it.unibo.ing.stormsmacs.rdfBindings.{GenericNodeResource, GenericNodeSample}
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
  private var persisted : Set[Int] = _
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
  override def typedExecute(t: (GenericNodeConf, Date, SigarMeteredData), st : Tuple): Unit = {
    try{
      val sampleData = GenericNodeSample(t._1.url, t._3)
      val sampleModel = sampleData.toRdf
      writeToRDFStore(GraphNamer.graphName(t._2), sampleModel)
      if (!(persisted contains t._1.url.toString.hashCode)) {
        val resourceData = GenericNodeResource(t._1.url, t._3)
        val resourceModel = resourceData.toRdf
        writeToRDFStore(GraphNamer.resourcesGraphName, resourceModel)
        persisted += t._1.url.toString.hashCode
      }
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
    val str = s"INSERT DATA { GRAPH $graphName { $dataAsString } }"

    val exchange = new ContentExchange()
    exchange.setURI(new URI(fusekiEndpoint.url / "update"))
    exchange.setMethod("POST")
    exchange.setRequestContentType("application/sparql-update")
    exchange.setRequestContent(new ByteArrayBuffer(str))
    httpClient.send(exchange)
    val state = exchange.waitForDone()
    if ((exchange.getResponseStatus/100) != 2){
      logger.info(str)
      throw new Exception(s"Cannot sparql update: ${exchange.getResponseStatus} -> ${exchange.getResponseContent}")
    }
  }
}