package it.unibo.ing.stormsmacs.rdfBindings

import it.unibo.ing.utils.{TimestampUtils, DateUtils}
import org.openstack.api.restful.ceilometer.v2.elements.{Resource, OldSample, Sample, Statistics}
import java.net.URL
import it.unibo.ing.utils._
import scala.language.postfixOps

/**
 * @author Antonio Murgia
 * @version 26/12/14
 */
case class OpenStackSampleData(url : URL, resource : Resource, info : Sample)

object OpenStackSampleDataRdfFormat{
  import it.unibo.ing.rdf.RdfWriter
  import it.unibo.ing.rdf.Properties
  import com.hp.hpl.jena.rdf.model._
  import com.hp.hpl.jena.vocabulary.RDF

  implicit object OpenStackSampleDataRdfWriter extends RdfWriter[OpenStackSampleData] {
    override def write(obj: OpenStackSampleData, absPath: String): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      //the sample
      val sample = m.createResource((obj.url / obj.info.id).toString)
      sample.addProperty(RDF.`type`, "Resource Sample")
      sample.addProperty(Properties.sampleType, "" + obj.info.`type`)
      sample.addProperty(Properties.sampleId, "" + obj.info.id)
      sample.addProperty(Properties.projectId, obj.info.project_id)
      sample.addProperty(Properties.recordedAt, TimestampUtils.format(obj.info.recorded_at))
      sample.addProperty(Properties.resourceId, obj.info.resource_id)
      sample.addProperty(Properties.source, obj.info.source)
      sample.addProperty(Properties.timestamp, TimestampUtils.format(obj.info.timestamp))
      sample.addProperty(Properties.unit, obj.info.unit)
      sample.addProperty(Properties.userId, obj.info.user_id)
      sample.addProperty(Properties.volume, "" + obj.info.volume)
      //the resource
      val resource = m.createResource((obj.url / obj.resource.resource_id).toString)
      if (obj.resource.project_id isDefined)
        resource.addProperty(Properties.projectId, obj.resource.project_id.get)
      resource.addProperty(Properties.source, obj.resource.source)
      if (obj.resource.user_id isDefined)
        resource.addProperty(Properties.userId, obj.resource.user_id.get)
      for (t <- obj.resource.metadata)
        if (t._2 nonEmpty)
          resource.addProperty(Properties.newProperty(t._1), t._2)

      return m
    }
  }
}
