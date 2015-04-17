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
    private var _persistedSample : Set[Long] = _
    setup{
      _persistedResources = Set()
      _persistedSample = Set()
    }
    shutdown{
      _persistedResources = null
      _persistedSample = null
    }

    protected def writeToRDF(graphName : String, model : Model)

    override def execute(t : Tuple) : Unit = {
      try{
        t match{
          case Seq(node: OpenStackNodeConf, date: Date, resource: Resource, sample: Sample) => {
            if (!(_persistedSample contains date.getTime)){
              val graphName = GraphNamer.graphName(date)
              val url = GraphNamer.cleanURL(node.ceilometerUrl)
              val data = OpenStackSampleData(url, sample)
              val model = data.toRdf
              writeToRDF(graphName, model)
              val res = OpenStackResourceData(GraphNamer.cleanURL(node.ceilometerUrl), resource, sample.meter, sample.unit, sample.`type`.toString)
              if (!(_persistedResources contains res.hashCode)){
                val resModel = res.toRdf
                writeToRDF(GraphNamer.resourcesGraphName, resModel)
                _persistedResources += res.hashCode
              }
              _persistedSample += date.getTime
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
}
