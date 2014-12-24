package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import java.io.ByteArrayOutputStream
import java.util.Date

import it.unibo.ing.utils.RDFUtils
import virtuoso.jena.driver._
import storm.scala.dsl.NonEmittingTypedBolt
import com.hp.hpl.jena.rdf.model.Model
import myUtils.DateUtils
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, CloudFoundryNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeData
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfConversion._
import it.unibo.ing.rdf._

/**
 * Created by tmnd91 on 24/12/14.
 */
class CloudFoundryNodePersisterBolt(fusekiEndpoint : FusekiNodeConf) extends NonEmittingTypedBolt[(CloudFoundryNodeConf, Date, MonitInfo)]{
  override def typedExecute(t: (CloudFoundryNodeConf, Date, MonitInfo)): Unit = {
    val graphName = RDFUtils.graphName(t._2)
    val data = CFNodeData(t._1.url, t._3)
    val model = data.toRdf(t._1.toString)
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
