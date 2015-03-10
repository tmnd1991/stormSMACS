package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.conf.{VirtuosoNodeConf, FusekiNodeConf}
import it.unibo.ing.stormsmacs.topologies.bolts.VirtuosoPersister
import storm.scala.dsl.Logging
import virtuoso.jena.driver.{VirtGraph, VirtuosoUpdateFactory}


/**
 * @author Antonio Murgia
 * @version 10/01/2015
 * Storm Bolt that persists the monitored values to a Virtuoso endpoint
 */
class OpenStackNodePersisterVirtuosoBolt(virtuosoEndpoint: VirtuosoNodeConf) extends OpenStackNodePersisterBolt(virtuosoEndpoint) with VirtuosoPersister
  with Logging{
  override def writeToRDF(graphName: String, model: Model): Unit = writeToRDFStore(virtuosoEndpoint, graphName, model)
}
