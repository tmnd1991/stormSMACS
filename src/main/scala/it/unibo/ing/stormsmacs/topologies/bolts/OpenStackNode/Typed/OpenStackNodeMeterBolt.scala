package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import java.net.{URL, URI}
import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, OpenStackNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackResourceData
import it.unibo.ing.utils._
import org.eclipse.jetty.client.{ContentExchange, HttpClient}
import org.eclipse.jetty.io.ByteArrayBuffer
import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource}
import org.openstack.clients.ceilometer.v2.CeilometerClient
import storm.scala.dsl.TypedBolt
import storm.scala.dsl.Logging

/**
 * @author Antonio Murgia
 * @version 22/12/14
 */
class OpenStackNodeMeterBolt(fusekiEndpoint : FusekiNodeConf, pollTime: Long)
  extends TypedBolt[(OpenStackNodeConf, Date, Resource),(OpenStackNodeConf, Date, Resource, Sample)](
    "NodeName", "GraphName", "Resource", "Sample")
  with Logging{
  private var _persistedResources : Set[Resource] = _
  private var httpClient: HttpClient = _
  setup {
    _persistedResources = Set()
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

  override def typedExecute(t: (OpenStackNodeConf, Date, Resource), st : Tuple) = {
    val cleanURL = clean(t._1.ceilometerUrl)
    persistResource(cleanURL, t._3)
    val cclient = CeilometerClient.getInstance(t._1.ceilometerUrl, t._1.keystoneUrl, t._1.tenantName, t._1.username, t._1.password, t._1.connectTimeout, t._1.readTimeout)
    val start = new Date(t._2.getTime - pollTime)
    cclient.tryGetSamplesOfResource(t._3.resource_id, start, t._2) match{
      case Some(Nil) => st.ack        //no samples for this resource, we just ack the tuple
      case Some(samples : Seq[Sample]) => for (s <- samples) using anchor st emit(t._1, t._2, t._3, s)
      case None => st.fail            //if we get None as a result, something bad happened, we need to replay the tuple
    }
  }
  private def persistResource(url : URL, r : Resource) : Boolean = {
    import it.unibo.ing.stormsmacs.rdfBindings.OpenStackRdfFormats._
    import it.unibo.ing.rdf._
    if (!(_persistedResources contains r)){
      val data = OpenStackResourceData(url,r)
      val dataAsString = data.toRdf.rdfSerialization("N-TRIPLE")
      val str = s"INSERT DATA { GRAPH Resources { $dataAsString } }"
      val exchange = new ContentExchange()
      exchange.setURI(new URI(fusekiEndpoint.url / "update"))
      exchange.setMethod("POST")
      exchange.setRequestContentType("application/sparql-update")
      exchange.setRequestContent(new ByteArrayBuffer(str))
      httpClient.send(exchange)
      val state = exchange.waitForDone()
      if ((exchange.getResponseStatus/100) != 2){
        logger.error(s"Cannot sparql update: ${exchange.getResponseStatus} -> ${exchange.getResponseContent}")
        false
      }
      else{
        _persistedResources += r
        true
      }
    }
    else
      true
  }
  private def clean (u : URL) = new URL(u.getProtocol + "://" + u.getHost / u.getPath)

}
