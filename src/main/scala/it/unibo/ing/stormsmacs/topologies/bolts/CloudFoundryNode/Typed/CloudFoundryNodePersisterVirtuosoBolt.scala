package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.{VirtuosoNodeConf}
import it.unibo.ing.stormsmacs.topologies.bolts.VirtuosoPersister

/**
 * Created by Antonio on 02/03/2015.
 */
class CloudFoundryNodePersisterVirtuosoBolt(virtuosoEndpoint : VirtuosoNodeConf)
  extends CloudFoundryNodePersisterBolt(virtuosoEndpoint)
  with VirtuosoPersister {

  override protected def writeToRDF(graphName: String, data: Model): Unit = writeToRDFStore(virtuosoEndpoint, graphName, data)
}

