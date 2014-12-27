package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, GenericNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeData
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeDataRdfFormat._
import it.unibo.ing.utils.RDFUtils
import it.unibo.ing.rdf._
import storm.scala.dsl.{Logging, StormTuple, TypedBolt}
import virtuoso.jena.driver.{VirtuosoUpdateFactory, VirtGraph}

/**
 * Created by tmnd91 on 24/12/14.
 */
class GenericNodePersisterBolt(fusekiEndpoint : FusekiNodeConf)
  extends TypedBolt[(GenericNodeConf, Date, SigarMeteredData), Nothing]
  with Logging
{
  override def typedExecute(t: (GenericNodeConf, Date, SigarMeteredData), st : Tuple): Unit = {
    try{
      val graphName = RDFUtils.graphName(t._2)
      val data = GenericNodeData(t._1.url, t._3)
      val model = data.toRdf()
      writeToRDFStore(graphName, model)
      st.ack
    }
    catch{
      case e: Throwable => {
        logger.error(e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }
  private def writeToRDFStore(graphName : String, data : Model) : Unit = {
    val dataAsString = data.rdfSerialization("N-TRIPLE")
    val set = new VirtGraph (fusekiEndpoint.url, fusekiEndpoint.username, fusekiEndpoint.password)
    val str = "INSERT INTO GRAPH " + graphName + " { " + dataAsString + "}"
    val vur = VirtuosoUpdateFactory.create(str, set)
    vur.exec()
  }
}