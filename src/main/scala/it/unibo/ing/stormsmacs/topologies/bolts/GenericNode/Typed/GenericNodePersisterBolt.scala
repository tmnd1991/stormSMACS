package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import java.util.Date
import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.rdf._
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{PersisterNodeConf, GenericNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.{GenericNodeResource, GenericNodeSample}
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeDataRdfFormat._


import storm.scala.dsl.additions.Logging
import storm.scala.dsl.StormBolt

/**
 * @author Antonio Murgia
 * @version 24/12/14.
 * Abstract Storm Bolt that persists the monitored values
 */
abstract class GenericNodePersisterBolt(persisterNode : PersisterNodeConf)
  extends StormBolt(List())
  //extends TypedBolt[(GenericNodeConf, Date, SigarMeteredData), Nothing]
  with Logging
{
  private var _persistedResources : Set[Int] = _
  private var _persistedSample : Set[Long] = _
  setup{
    _persistedResources = Set()
    _persistedSample = Set()
  }
  shutdown{
    _persistedResources = null
    _persistedSample = null
  }
  setup {
    _persistedResources = Set()
  }
  shutdown{
    _persistedResources = null
  }
  override def execute(t : Tuple) : Unit = {
    try{
      t matchSeq{
        case Seq(node: GenericNodeConf, date: Date, data: SigarMeteredData) =>{
          if (!(_persistedSample contains date.getTime)){
            val sampleData = GenericNodeSample(node.url, data)
            val sampleModel = sampleData.toRdf
            writeToRDF(GraphNamer.graphName(date), sampleModel)
            if (!(_persistedResources contains node.url.toString.hashCode)) {
              val resourceData = GenericNodeResource(node.url, data)
              val resourceModel = resourceData.toRdf
              writeToRDF(GraphNamer.resourcesGraphName, resourceModel)
              _persistedResources += node.url.toString.hashCode
            }
            _persistedSample += date.getTime
          }
          t ack
        }
      }
    }
    catch{
      case e: Throwable => {
        logger.trace(e.getMessage, e)
        t fail
      }
    }
  }

  protected def writeToRDF(graphName : String, data : Model) : Unit
}