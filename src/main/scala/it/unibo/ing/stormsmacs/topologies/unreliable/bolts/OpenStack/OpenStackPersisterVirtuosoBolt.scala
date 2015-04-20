package it.unibo.ing.stormsmacs.topologies.unreliable.bolts.OpenStack

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.VirtuosoNodeConf
import it.unibo.ing.stormsmacs.topologies.facilities.VirtuosoPersister
import storm.scala.dsl.additions.Logging


/**
  * @author Antonio Murgia
  * @version 10/01/2015
  * Storm Bolt that persists the monitored values to a Virtuoso endpoint
  */
class OpenStackPersisterVirtuosoBolt(virtuosoEndpoint: VirtuosoNodeConf) extends OpenStackPersisterBolt(virtuosoEndpoint) with VirtuosoPersister
   with Logging{
   override def writeToRDF(graphName: String, model: Model): Unit = writeToRDFStore(virtuosoEndpoint, graphName, model)
 }
