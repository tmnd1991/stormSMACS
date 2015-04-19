package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.FusekiNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.FusekiPersister
import org.eclipse.jetty.client.HttpClient

/**
 * @author Antonio Murgia
 * @version 24/12/14.
 * Storm Bolt that persists the monitored values to given Fuseki endpoint
 */
class GenericNodePersisterFusekiBolt(fusekiEndpoint : FusekiNodeConf)
  extends GenericNodePersisterBolt(fusekiEndpoint) with FusekiPersister
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

  override protected def writeToRDF(graphName: String, data: Model) : Unit = writeToRDFStore(fusekiEndpoint, httpClient, graphName, data)
}