package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.FusekiNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.FusekiPersister
import org.eclipse.jetty.client.HttpClient

/**
 * Created by tmnd91 on 24/12/14.
 */
class CloudFoundryNodePersisterFusekiBolt(fusekiEndpoint : FusekiNodeConf)
  extends CloudFoundryNodePersisterBolt(fusekiEndpoint)
  with FusekiPersister {

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

  override protected def writeToRDF(graphName: String, data: Model): Unit = writeToRDFStore(fusekiEndpoint, httpClient, graphName, data)
}
