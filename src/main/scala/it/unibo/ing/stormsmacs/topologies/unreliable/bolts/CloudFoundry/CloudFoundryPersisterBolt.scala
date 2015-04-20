package it.unibo.ing.stormsmacs.topologies.unreliable.bolts.CloudFoundry

import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{CloudFoundryNodeConf, PersisterNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfFormat._
import it.unibo.ing.stormsmacs.rdfBindings.{CFNodeResource, CFNodeSample}
import it.unibo.ing.utils.MostRecentKvalues
import storm.scala.dsl.StormBolt
import storm.scala.dsl.additions.Logging

/**
  * @author Antonio Murgia
  * @version 24/12/14
  * Abstract Storm Bolt that persists the monitored values
  */
abstract class CloudFoundryPersisterBolt(persisterEndpoint : PersisterNodeConf)
  extends StormBolt(List()) with Logging{
     private var _persistedResources : Set[Int] = _
     setup{
       _persistedResources = Set()
     }
     shutdown{
       _persistedResources = null
     }
     override def execute(t : Tuple) = t matchSeq {
       case Seq(node: CloudFoundryNodeConf, date: Date, info: MonitInfo) => {
         try {
           val sId = node.id + info.name
           val graphName = GraphNamer.graphName(date)
           val sampleData = CFNodeSample(node.url, info)
           writeToRDF(graphName, sampleData.toRdf)
           if (!(_persistedResources contains info.resId)) {
             val resourceData = CFNodeResource(node.url, info)
             writeToRDF(GraphNamer.resourcesGraphName, resourceData.toRdf)
             _persistedResources += info.resId
           }
         }
         catch {
           case r: RuntimeException =>
             logger.error(r.getMessage, r)
             logger.error("fail - non persisted " + date)
           case e: Throwable =>
             logger.error(e.getMessage, e)
             logger.error("fail - non persisted " + date)
         }
       }
     }
     protected def writeToRDF(graphName : String, data : Model) : Unit
   }
