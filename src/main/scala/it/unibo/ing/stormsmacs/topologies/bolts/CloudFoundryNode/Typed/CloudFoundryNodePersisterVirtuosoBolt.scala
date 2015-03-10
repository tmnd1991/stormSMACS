package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.{VirtuosoNodeConf}
import it.unibo.ing.stormsmacs.topologies.bolts.VirtuosoPersister

/**
 * @author Antonio Murgia
 * @version 24/12/14
 * Storm Bolt that persists the monitored values to a Virtuoso endpoint
 */
class CloudFoundryNodePersisterVirtuosoBolt(virtuosoEndpoint : VirtuosoNodeConf)
  extends CloudFoundryNodePersisterBolt(virtuosoEndpoint)
  with VirtuosoPersister {

  override protected def writeToRDF(graphName: String, data: Model): Unit = writeToRDFStore(virtuosoEndpoint, graphName, data)
}

