package it.unibo.ing.stormsmacs.topologies.reliable.bolts.CloudFoundry

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.{VirtuosoNodeConf}
import it.unibo.ing.stormsmacs.topologies.facilities.VirtuosoPersister

/**
 * @author Antonio Murgia
 * @version 24/12/14
 * Storm Bolt that persists the monitored values to a Virtuoso endpoint
 */
class CloudFoundryPersisterVirtuosoBolt(virtuosoEndpoint : VirtuosoNodeConf)
  extends CloudFoundryPersisterBolt(virtuosoEndpoint)
  with VirtuosoPersister {
  override protected def writeToRDF(graphName: String, data: Model): Unit = writeToRDFStore(virtuosoEndpoint, graphName, data)
}

