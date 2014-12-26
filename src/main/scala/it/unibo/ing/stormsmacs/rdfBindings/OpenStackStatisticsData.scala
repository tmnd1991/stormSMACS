package it.unibo.ing.stormsmacs.rdfBindings

import it.unibo.ing.utils.{TimestampUtils, DateUtils}
import org.openstack.api.restful.ceilometer.v2.elements.{OldSample, Sample, Statistics}
import java.net.URL
/**
 * @author Antonio Murgia
 * @version 26/12/14
 */
case class OpenStackStatisticsData(url : URL, meterName : String, info : Statistics)
case class OpenStackSampleData(url : URL, meterName : String, info : Sample)
case class OpenStackOldSampleData(url : URL, meterName : String, info : OldSample)

object OpenStackStatisticsDataRdfFormat{
  import it.unibo.ing.rdf.RdfWriter
  import it.unibo.ing.rdf.Properties
  import com.hp.hpl.jena.rdf.model._
  import com.hp.hpl.jena.vocabulary.RDF
  implicit object OpenStackStatisticsDataRdfWriter extends RdfWriter[OpenStackStatisticsData] {
    override def write(obj: OpenStackStatisticsData, absPath: String): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      val r = m.createResource(obj.url.toString + "/" + obj.meterName)
      r.addProperty(RDF.`type`, "OpenStack Node Statistics")
      r.addProperty(Properties.averageValue, "" + obj.info.avg)
      r.addProperty(Properties.countValue, "" + obj.info.count)
      r.addProperty(Properties.duration, "" + obj.info.duration)
      r.addProperty(Properties.durationEnd, DateUtils.format(obj.info.duration_end))
      r.addProperty(Properties.durationStart, DateUtils.format(obj.info.duration_start))
      r.addProperty(Properties.maxValue, "" + obj.info.max)
      r.addProperty(Properties.minValue, "" + obj.info.min)
      r.addProperty(Properties.period, "" + obj.info.period)
      r.addProperty(Properties.periodEnd, "" + obj.info.period_end)
      r.addProperty(Properties.periodStart, "" + obj.info.period_start)
      r.addProperty(Properties.sumValue, "" + obj.info.sum)
      r.addProperty(Properties.unit, "" + obj.info.unit)
      m
    }
  }
  implicit object OpenStackSampleDataRdfWriter extends RdfWriter[OpenStackSampleData] {
    override def write(obj: OpenStackSampleData, absPath: String): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      val r = m.createResource(obj.url.toString + "/" + obj.meterName)
      r.addProperty(RDF.`type`, "OpenStack Node Sample")
      r.addProperty(Properties.sampleType, "" + obj.info.`type`)
      r.addProperty(Properties.sampleId, "" + obj.info.id)
      r.addProperty(Properties.projectId, obj.info.project_id)

      r.addProperty(Properties.recordedAt, TimestampUtils.format(obj.info.recorded_at))
      r.addProperty(Properties.resourceId, obj.info.resource_id)
      r.addProperty(Properties.source, obj.info.source)
      r.addProperty(Properties.timestamp, TimestampUtils.format(obj.info.timestamp))
      r.addProperty(Properties.unit, obj.info.unit)
      r.addProperty(Properties.userId, obj.info.user_id)
      r.addProperty(Properties.volume, "" + obj.info.volume)
      //obj.info.metadata i will not map this right now
      m
    }
  }

  implicit object OpenStackOldSampleDataRdfWriter extends RdfWriter[OpenStackOldSampleData] {
    override def write(obj: OpenStackOldSampleData, absPath: String): Model = {
      val m = ModelFactory.createDefaultModel()
      m.setNsPrefixes(Properties.prefixes)
      val r = m.createResource(obj.url.toString + "/" + obj.meterName)
      r.addProperty(RDF.`type`, "OpenStack Node Old Sample")
      r.addProperty(Properties.counterName, obj.info.counter_name)
      r.addProperty(Properties.counterType, "" + obj.info.counter_type)
      r.addProperty(Properties.counterUnit, obj.info.counter_unit)
      r.addProperty(Properties.counterVolume, "" + obj.info.counter_volume)
      r.addProperty(Properties.messageId, obj.info.message_id)
      r.addProperty(Properties.projectId, obj.info.project_id)
      r.addProperty(Properties.recordedAt, TimestampUtils.format(obj.info.recorded_at))
      r.addProperty(Properties.resourceId, obj.info.resource_id)
      r.addProperty(Properties.source, obj.info.source)
      r.addProperty(Properties.timestamp, TimestampUtils.format(obj.info.timestamp))
      r.addProperty(Properties.userId, obj.info.user_id)
      //obj.info.resource_metadata i will not map this right now
      m
    }
  }

}
