package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.FusekiNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.FusekiPersister
import org.eclipse.jetty.client.HttpClient

/**
 * Created by tmnd91 on 10/01/15.
 */
class OpenStackNodePersisterFusekiBolt(fusekiEndpoint: FusekiNodeConf) extends OpenStackNodePersisterBolt(fusekiEndpoint) with FusekiPersister{
  private var httpClient: HttpClient = _
  private var persisted : Set[Int] = _
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
  override def writeToRDF(graphName: String, model: Model): Unit = writeToRDFStore(fusekiEndpoint, httpClient, graphName, model)
}
