package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

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
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.util.StringContentProvider
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
    httpClient.setFollowRedirects(false)
    httpClient.setStopTimeout(1000)
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
        logger.error(e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }
  private def writeToRDFStore(graphName : String, data : Model) : Unit = {
    val dataAsString = data.rdfSerialization("N-TRIPLE")
    val str = "INSERT DATA { GRAPH " + graphName + " { " + dataAsString + "} }"
    val resp = httpClient.POST(fusekiEndpoint.url / "update").
      header("Content-Type", "application/sparql-update").
      content(new StringContentProvider(str)).
      send()
    if ((resp.getStatus/100) != 2)
      throw new Exception(s"Cannot sparql update: {resp.getStatus} -> {resp.getContentAsString}")
  }
}