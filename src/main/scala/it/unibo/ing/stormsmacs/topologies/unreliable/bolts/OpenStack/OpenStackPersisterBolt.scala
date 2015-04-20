package it.unibo.ing.stormsmacs.topologies.unreliable.bolts.OpenStack

import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{OpenStackNodeConf, PersisterNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackRdfFormats._
import it.unibo.ing.stormsmacs.rdfBindings.{OpenStackResourceData, OpenStackSampleData}
import it.unibo.ing.utils._
import org.openstack.api.restful.ceilometer.v2.elements.{Resource, Sample}
import storm.scala.dsl.StormBolt
import storm.scala.dsl.additions.Logging


/**
 * @author Antonio Murgia
 * @version 22/12/2014
 * Abstract Storm Bolt that persists the monitored values
 */
abstract class OpenStackPersisterBolt(persisterNode: PersisterNodeConf)
  extends StormBolt(List())
  with Logging{
    private var _persistedResources : Set[Int] = _
    setup{
      _persistedResources = Set()
    }
    shutdown{
      _persistedResources = null
    }

    protected def writeToRDF(graphName : String, model : Model)

    override def execute(t : Tuple) : Unit ={
      t matchSeq {
        case Seq(node: OpenStackNodeConf, date: Date, resource: Resource, sample: Sample) => {
          try {
            val sId = sample.id + resource.resource_id
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
          }
          catch{
            case e : Throwable =>
              logger.error(e.getMessage,e)
              logger.info("fail " + date)
          }
        }
      }
    }
}
