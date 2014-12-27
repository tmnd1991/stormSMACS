package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import java.util.Date

import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, GenericNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeData
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeDataRdfFormat._
import it.unibo.ing.utils.RDFUtils
import it.unibo.ing.rdf._
import storm.scala.dsl.NonEmittingTypedBolt
import virtuoso.jena.driver.{VirtuosoUpdateFactory, VirtGraph}

/**
 * Created by tmnd91 on 24/12/14.
 */
class GenericNodePersisterBolt(fusekiEndpoint : FusekiNodeConf) extends NonEmittingTypedBolt[(GenericNodeConf, Date, SigarMeteredData)]{
  override def typedExecute(t: (GenericNodeConf, Date, SigarMeteredData)): Unit = {
    val graphName = RDFUtils.graphName(t._2)
    val data = GenericNodeData(t._1.url, t._3)
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