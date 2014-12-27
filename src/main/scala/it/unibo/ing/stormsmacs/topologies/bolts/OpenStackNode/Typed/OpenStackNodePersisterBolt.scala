package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import java.util.Date
import java.net.URL
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, OpenStackNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackStatisticsData
import it.unibo.ing.stormsmacs.rdfBindings.OpenStackStatisticsDataRdfFormat._
import it.unibo.ing.utils.RDFUtils
import it.unibo.ing.rdf._
import org.openstack.api.restful.ceilometer.v2.elements.Statistics
import storm.scala.dsl.{NonEmittingTypedBolt}
import virtuoso.jena.driver.{VirtuosoUpdateFactory, VirtGraph}

/**
 * Created by tmnd91 on 22/12/14.
 */
class OpenStackNodePersisterBolt(fusekiEndpoint: FusekiNodeConf) extends NonEmittingTypedBolt[(OpenStackNodeConf, Date, String, Statistics)] {
  override def typedExecute(t: (OpenStackNodeConf, Date, String, Statistics)) = {
    val graphName = RDFUtils.graphName(t._2)
    val url = new URL(t._1.ceilometerUrl.getProtocol + "://" +t._1.ceilometerUrl.getHost)
    val data = OpenStackStatisticsData(url, t._3, t._4)
    val model = data.toRdf()
    writeToRDFStore(graphName, model)
  }
  private def writeToRDFStore(graphName : String, data : Model) : Unit = {
    val dataAsString = data.rdfSerialization("N-TRIPLE")
    val set = new VirtGraph (fusekiEndpoint.url, fusekiEndpoint.username, fusekiEndpoint.password)
    val str = "INSERT INTO GRAPH " + graphName + " { " + dataAsString + "}"
    val vur = VirtuosoUpdateFactory.create(str, set)
    vur.exec()
  }
}
