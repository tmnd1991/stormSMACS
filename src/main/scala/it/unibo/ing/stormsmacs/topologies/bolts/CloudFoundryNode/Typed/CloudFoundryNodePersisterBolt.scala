package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import java.io.ByteArrayOutputStream
import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.GraphNamer
import virtuoso.jena.driver._
import storm.scala.dsl.{StormTuple, Logging, TypedBolt}
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
class CloudFoundryNodePersisterBolt(fusekiEndpoint : FusekiNodeConf)
  extends TypedBolt[(CloudFoundryNodeConf, Date, MonitInfo), Nothing]
  with Logging{
  override def typedExecute(t: (CloudFoundryNodeConf, Date, MonitInfo), st : Tuple): Unit = {
    try{
      val graphName = GraphNamer.graphName(t._2)
      val data = CFNodeData(t._1.url, t._3)
      val model = data.toRdf(t._1.toString)
      writeToRDFStore(graphName, model)
      st.ack
    }
    catch{
      case e : Throwable => {
        logger.error(e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }

  private def writeToRDFStore(graphName : String, data : Model) : Unit = {
    val dataAsString = data.rdfSerialization("N-TRIPLE")
    val set = new VirtGraph (fusekiEndpoint.url, fusekiEndpoint.username, fusekiEndpoint.password)
    val str = "INSERT DATA { GRAPH " + graphName + " { " + dataAsString + "} }"
    val vur = VirtuosoUpdateFactory.create(str, set)
    vur.exec()
  }
}
