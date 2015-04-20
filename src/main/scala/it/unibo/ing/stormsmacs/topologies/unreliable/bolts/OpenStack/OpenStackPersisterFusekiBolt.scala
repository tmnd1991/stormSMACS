package it.unibo.ing.stormsmacs.topologies.unreliable.bolts.OpenStack

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.FusekiNodeConf
import it.unibo.ing.stormsmacs.topologies.facilities.FusekiPersister
import org.eclipse.jetty.client.HttpClient

/**
  * @author Antonio Murgia
  * @version 10/01/2015
  * Storm Bolt that persists the monitored values to a Fuseki endpoint
  */
class OpenStackPersisterFusekiBolt(fusekiEndpoint: FusekiNodeConf) extends OpenStackPersisterBolt(fusekiEndpoint) with FusekiPersister{
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
