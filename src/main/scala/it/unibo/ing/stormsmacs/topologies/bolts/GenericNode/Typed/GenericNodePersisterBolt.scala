package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import java.net.URI
import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.rdf._
import it.unibo.ing.utils._
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.{PersisterNodeConf, FusekiNodeConf, GenericNodeConf}
import it.unibo.ing.stormsmacs.rdfBindings.{GenericNodeResource, GenericNodeSample}
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeDataRdfFormat._
import org.eclipse.jetty.client.{ContentExchange, HttpClient}
import org.eclipse.jetty.io.ByteArrayBuffer
import storm.scala.dsl.{Logging, TypedBolt}

/**
 * @author Antonio Murgia
 * @version 24/12/14.
 * Abstract Storm Bolt that persists the monitored values
 */
abstract class GenericNodePersisterBolt(persisterNode : PersisterNodeConf)
  extends TypedBolt[(GenericNodeConf, Date, SigarMeteredData), Nothing]
  with Logging
{
  private var persisted : Set[Int] = _
  setup {
    persisted = Set()
  }
  shutdown{
    persisted = null
  }
  override def typedExecute(t: (GenericNodeConf, Date, SigarMeteredData), st : Tuple): Unit = {
    try{
      val sampleData = GenericNodeSample(t._1.url, t._3)
      val sampleModel = sampleData.toRdf
      writeToRDF(GraphNamer.graphName(t._2), sampleModel)
      if (!(persisted contains t._1.url.toString.hashCode)) {
        val resourceData = GenericNodeResource(t._1.url, t._3)
        val resourceModel = resourceData.toRdf
        writeToRDF(GraphNamer.resourcesGraphName, resourceModel)
        persisted += t._1.url.toString.hashCode
      }
      st.ack
    }
    catch{
      case e: Throwable => {
        logger.error(e.getMessage + "\n" + e.getStackTrace.mkString("\n"))
        st.fail
      }
    }
  }

  protected def writeToRDF(graphName : String, data : Model) : Unit
}