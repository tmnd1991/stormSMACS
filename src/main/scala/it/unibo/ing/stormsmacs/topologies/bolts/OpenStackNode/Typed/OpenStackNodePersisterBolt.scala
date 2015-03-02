package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{PersisterNodeConf, OpenStackNodeConf, FusekiNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.{OpenStackResourceData, OpenStackSampleData}

import org.openstack.api.restful.ceilometer.v2.elements.{Sample, Resource}
import storm.scala.dsl.{Logging, TypedBolt}

import it.unibo.ing.rdf._
import it.unibo.ing.utils._
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackRdfFormats._


/**
 * Created by Antonio on 02/03/2015.
 */
abstract class OpenStackNodePersisterBolt(persisterNode: PersisterNodeConf)
  extends TypedBolt[(OpenStackNodeConf, Date, Resource, Sample), Nothing]
  with Logging{
    private var _persisted : Set[Int] = _

    protected def writeToRDF(graphName : String, model : Model)

    override def typedExecute(t: (OpenStackNodeConf, Date, Resource, Sample), st: Tuple): Unit = {
      try {
        val graphName = GraphNamer.graphName(t._2)
        val url = GraphNamer.cleanURL(t._1.ceilometerUrl)
        val data = OpenStackSampleData(url, t._3.resource_id, t._4)
        val model = data.toRdf
        writeToRDF(graphName, model)
        val res = OpenStackResourceData(GraphNamer.cleanURL(t._1.ceilometerUrl), t._3, t._4.meter, t._4.unit, t._4.`type`.toString)
        if (!(_persisted contains res.hashCode)){
          val resModel = res.toRdf
          writeToRDF(GraphNamer.resourcesGraphName, resModel)
          _persisted += res.hashCode
        }
        st.ack
      }
      catch {
        case e: Throwable => {
          logger.error(e.getMessage + "\n" + e.getStackTrace.mkString("\n"))
          st.fail
        }
      }
    }
}
