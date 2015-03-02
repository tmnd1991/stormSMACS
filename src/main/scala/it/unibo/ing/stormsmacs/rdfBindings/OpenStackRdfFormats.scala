package it.unibo.ing.stormsmacs.rdfBindings

import scala.language.postfixOps
import java.net.URL
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.rdf.model._
import it.unibo.ing.utils._
import it.unibo.ing.rdf.RdfWriter
import it.unibo.ing.rdf.Properties
import org.openstack.api.restful.ceilometer.v2.elements.{Meter, Resource, Sample}

/**
 * @author Antonio Murgia
 * @version 26/12/14
 */
case class OpenStackSampleData(url : URL, resourceId : String, info : Sample)
case class OpenStackResourceData(url : URL, resource : Resource, meterId : String, unit : String, `type` : String)
object OpenStackRdfFormats{

  implicit object OpenStackSampleDataRdfWriter extends RdfWriter[OpenStackSampleData] {
    override def write(obj: OpenStackSampleData): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      //the sample
      val sample = m.createResource((obj.url / obj.info.id).toString).
        addProperty(RDF.`type`, "Sample").
        addProperty(Properties.sampleType, "" + obj.info.`type`). //we can skip this
        addProperty(Properties.sampleId, "" + obj.info.id).       //this too
        addProperty(Properties.projectId, obj.info.project_id).
        addProperty(Properties.recordedAt, TimestampUtils.format(obj.info.recorded_at)).
        addProperty(Properties.resourceId, obj.resourceId).
        addProperty(Properties.source, obj.info.source).
        addProperty(Properties.timestamp, TimestampUtils.format(obj.info.timestamp)).
        addProperty(Properties.unit, obj.info.unit).
        addProperty(Properties.userId, obj.info.user_id).
        addProperty(RDF.value, "" + obj.info.volume)
      return m
    }
  }
  implicit object OpenStackResourceRdfWriter extends RdfWriter[OpenStackResourceData] {
    override def write(obj: OpenStackResourceData): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      val resourceURL = (obj.url / obj.resource.resource_id / obj.meterId).toString
      val resource = m.createResource(resourceURL).
        addProperty(RDF.`type`, "Resource").
        addProperty(Properties.unit, obj.unit).
        addProperty(Properties.sampleType, "" + obj.`type`)
      if (obj.resource.project_id isDefined)
        resource.addProperty(Properties.projectId, obj.resource.project_id.get)
      if (obj.resource.user_id isDefined)
        resource.addProperty(Properties.userId, obj.resource.user_id.get)
      for (t <- obj.resource.metadata)
        if (t._2 nonEmpty)
          resource.addProperty(Properties.newProperty(t._1), t._2)
      return m
    }
  }
}
