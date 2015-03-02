package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.FusekiNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.VirtuosoPersister

/**
 * Created by tmnd91 on 24/12/14.
 */
class GenericNodePersisterVirtuosoBolt(fusekiEndpoint : FusekiNodeConf)
  extends GenericNodePersisterBolt(fusekiEndpoint)
  with VirtuosoPersister
{
  override protected def writeToRDF(graphName: String, data: Model): Unit = writeToRDFStore(fusekiEndpoint, graphName, data)
}