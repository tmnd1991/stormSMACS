package it.unibo.ing.stormsmacs.topologies.reliable.bolts.Generic

import java.util.Date
import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.rdf._
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{PersisterNodeConf, GenericNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.{GenericNodeResource, GenericNodeSample}
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeDataRdfFormat._
import it.unibo.ing.utils.MostRecentKvalues


import storm.scala.dsl.additions.Logging
import storm.scala.dsl.StormBolt

/**
 * @author Antonio Murgia
 * @version 24/12/14.
 * Abstract Storm Bolt that persists the monitored values
 */
abstract class GenericPersisterBolt(persisterNode : PersisterNodeConf)
  extends StormBolt(List())
  with Logging
{
  private var _persistedResources : Set[Int] = _
  private var _persistedSamples : MostRecentKvalues[List[String]] = _
  setup{
    _persistedResources = Set()
    _persistedSamples = new MostRecentKvalues[List[String]](30)
  }
  shutdown{
    _persistedResources = null
    _persistedSamples = null
  }
  setup {
    _persistedResources = Set()
  }
  shutdown{
    _persistedResources = null
  }
  override def execute(t : Tuple) : Unit = t matchSeq{
    case Seq(node: GenericNodeConf, date: Date, data: SigarMeteredData) =>{
      try {
        val sId = node.id
        if (!((_persistedSamples contains date) && (_persistedSamples(date) contains sId))) {
          val sampleData = GenericNodeSample(node.url, data)
          val sampleModel = sampleData.toRdf
          writeToRDF(GraphNamer.graphName(date), sampleModel)
          if (!(_persistedResources contains node.url.toString.hashCode)) {
            val resourceData = GenericNodeResource(node.url, data)
            val resourceModel = resourceData.toRdf
            writeToRDF(GraphNamer.resourcesGraphName, resourceModel)
            _persistedResources += node.url.toString.hashCode
          }
          if (!(_persistedSamples contains date))
            _persistedSamples(date) = List(sId)
          else
            _persistedSamples(date) :+= sId
        }
        //logger.info("ack " + date)
        t ack
      }
      catch {
        case r: RuntimeException =>
          logger.error(r.getMessage,r)
          logger.error("fail " + date)
          t fail
        case e: Throwable =>
          logger.error(e.getMessage,e)
          logger.error("fail " + date)
          t fail
      }
    }
  }

  protected def writeToRDF(graphName : String, data : Model) : Unit
}