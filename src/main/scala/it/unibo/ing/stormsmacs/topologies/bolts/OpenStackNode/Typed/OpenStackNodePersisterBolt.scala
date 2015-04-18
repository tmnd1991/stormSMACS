package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{PersisterNodeConf, OpenStackNodeConf, FusekiNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.{OpenStackResourceData, OpenStackSampleData}

import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource}
import storm.scala.dsl.additions.Logging
import storm.scala.dsl.StormBolt

import it.unibo.ing.rdf._
import it.unibo.ing.utils._
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackRdfFormats._


/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Abstract Storm Bolt that persists the monitored values
 */
abstract class OpenStackNodePersisterBolt(persisterNode: PersisterNodeConf)
  extends StormBolt(List())
  with Logging{
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

    protected def writeToRDF(graphName : String, model : Model)

    override def execute(t : Tuple) : Unit = try {
      t matchSeq {
        case Seq(node: OpenStackNodeConf, date: Date, resource: Resource, sample: Sample) => {
          val sId = sample.id + resource.resource_id
          if (!((_persistedSamples contains date) && (_persistedSamples(date) contains sId))) {
            val graphName = GraphNamer.graphName(date)
            val url = GraphNamer.cleanURL(node.ceilometerUrl)
            val data = OpenStackSampleData(url, sample)
            val model = data.toRdf
            writeToRDF(graphName, model)
            val res = OpenStackResourceData(GraphNamer.cleanURL(node.ceilometerUrl), resource, sample.meter, sample.unit, sample.`type`.toString)
            if (!(_persistedResources contains res.hashCode)) {
              val resModel = res.toRdf
              writeToRDF(GraphNamer.resourcesGraphName, resModel)
              _persistedResources += res.hashCode
            }
            if (!(_persistedSamples contains date))
              _persistedSamples(date) = List(sId)
            else
              _persistedSamples(date) :+= sId
          }
          t ack
        }
      }
    }
    catch {
      case e: Throwable => {
        logger.trace(e.getMessage, e)
        t fail
      }
    }
}
