package it.unibo.ing.stormsmacs.topologies.bolts.CloudFoundryNode.Typed

import java.io.ByteArrayOutputStream
import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.stormsmacs.GraphNamer
import virtuoso.jena.driver._
import storm.scala.dsl.{StormTuple, Logging, TypedBolt}
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.stormsmacs.conf.{PersisterNodeConf, FusekiNodeConf, CloudFoundryNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.{CFNodeResource, CFNodeSample}
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfFormat._
import it.unibo.ing.rdf._

/**
 * @author Antonio Murgia
 * @version 24/12/14
 * Abstract Storm Bolt that persists the monitored values
 */
abstract class CloudFoundryNodePersisterBolt(persisterEndpoint : PersisterNodeConf)
  extends TypedBolt[(CloudFoundryNodeConf, Date, MonitInfo), Nothing]
  with Logging{
  private var persisted : Set[Int] = _
  setup{
    persisted = Set()
  }
  shutdown{
    persisted = null
  }
  override def typedExecute(t: (CloudFoundryNodeConf, Date, MonitInfo), st : Tuple): Unit = {
    try{
      val graphName = GraphNamer.graphName(t._2)
      val sampleData = CFNodeSample(t._1.url, t._3)
      writeToRDF(graphName, sampleData.toRdf)
      if (!(persisted contains t._3.resId)){
        val resourceData = CFNodeResource(t._1.url, t._3)
        writeToRDF(GraphNamer.resourcesGraphName, resourceData.toRdf)
        persisted += t._3.resId
      }
      st.ack
    }
    catch{
      case e : Throwable => {
        logger.error(e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }

  protected def writeToRDF(graphName : String, data : Model) : Unit
}
