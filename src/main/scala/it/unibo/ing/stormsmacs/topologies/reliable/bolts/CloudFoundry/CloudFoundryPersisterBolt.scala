package it.unibo.ing.stormsmacs.topologies.reliable.bolts.CloudFoundry

import java.io.ByteArrayOutputStream
import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.utils.MostRecentKvalues
import storm.scala.dsl.additions.Logging
import virtuoso.jena.driver._
import storm.scala.dsl.StormBolt
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.stormsmacs.conf.{PersisterNodeConf, FusekiNodeConf, CloudFoundryNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.{CFNodeResource, CFNodeSample}
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfFormat._
import it.unibo.ing.rdf._

/**
 * @author Antonio Murgia
 * @version 24/12/14
 * Abstract Storm Bolt that persists the monitored values
 */
abstract class CloudFoundryPersisterBolt(persisterEndpoint : PersisterNodeConf)
  extends StormBolt(List()) with Logging{
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


  override def execute(t : Tuple) = t matchSeq {
    case Seq(node: CloudFoundryNodeConf, date: Date, info: MonitInfo) => {
      try {
        val sId = node.id + info.name
        if (!((_persistedSamples contains date) && (_persistedSamples(date) contains sId))){
          val graphName = GraphNamer.graphName(date)
          val sampleData = CFNodeSample(node.url, info)
          writeToRDF(graphName, sampleData.toRdf)
          if (!(_persistedResources contains info.resId)) {
            val resourceData = CFNodeResource(node.url, info)
            writeToRDF(GraphNamer.resourcesGraphName, resourceData.toRdf)
            _persistedResources += info.resId
          }
          if (!(_persistedSamples contains date))
            _persistedSamples(date) = List(sId)
          else
            _persistedSamples(date) :+= sId
        }
        t ack
      }
      catch {
        case r: RuntimeException =>
          logger.error(r.getMessage, r)
          logger.error("fail - non persisted " + date)
          t fail
      }
    }
  }
  protected def writeToRDF(graphName : String, data : Model) : Unit
}
