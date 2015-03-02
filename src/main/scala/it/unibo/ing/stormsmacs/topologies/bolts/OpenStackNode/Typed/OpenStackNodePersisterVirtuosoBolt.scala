package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.conf.FusekiNodeConf
import it.unibo.ing.stormsmacs.topologies.bolts.VirtuosoPersister
import storm.scala.dsl.Logging
import virtuoso.jena.driver.{VirtGraph, VirtuosoUpdateFactory}

/**
 * Created by tmnd91 on 22/12/14.
 */
class OpenStackNodePersisterVirtuosoBolt(fusekiEndpoint: FusekiNodeConf) extends OpenStackNodePersisterBolt(fusekiEndpoint: FusekiNodeConf) with VirtuosoPersister
  with Logging{
  override def writeToRDF(graphName: String, model: Model): Unit = writeToRDFStore(fusekiEndpoint, graphName, model)
}
