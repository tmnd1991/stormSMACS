package it.unibo.ing.stormsmacs.topologies.bolts.Debug

import java.io.PrintWriter
import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.GenericNodeConf
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeSample
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeResource
import it.unibo.ing.stormsmacs.rdfBindings.GenericNodeDataRdfFormat._
import it.unibo.ing.rdf._
import storm.scala.dsl.{Logging, TypedBolt}

/**
 * Created by tmnd91 on 02/01/15.
 */
class genWriteToFileBolt(file : String) extends TypedBolt[(GenericNodeConf, Date, SigarMeteredData), Nothing]
with Logging
{
  private var _writer : PrintWriter = null
  setup{
    _writer = new PrintWriter(file)
  }
  shutdown{
    _writer.close
  }
  override def typedExecute(t: (GenericNodeConf, Date, SigarMeteredData), st : Tuple): Unit = {
    try{
      val graphName = GraphNamer.graphName(t._2)
      val sample = GenericNodeSample(t._1.url, t._3)
      val resource = GenericNodeResource(t._1.url, t._3)
      val sampleModel = sample.toRdf
      val resourceModel = resource.toRdf
      writeToRDFStore("Resource", resourceModel)
      writeToRDFStore(graphName, sampleModel)
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
    data.write(_writer, "N-TRIPLE")
  }
}