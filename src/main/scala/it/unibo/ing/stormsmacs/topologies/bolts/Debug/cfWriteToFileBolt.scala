package it.unibo.ing.stormsmacs.topologies.bolts.Debug

import java.io.{PrintWriter, OutputStreamWriter, BufferedWriter, File}
import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.CloudFoundryNodeConf
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfFormat._
import it.unibo.ing.stormsmacs.rdfBindings.{CFNodeResource, CFNodeSample}
import storm.scala.dsl.{Logging, TypedBolt}
import it.unibo.ing.rdf._

class cfWriteToFileBolt(file : String) extends TypedBolt[(CloudFoundryNodeConf, Date, MonitInfo), Nothing]
with Logging{
  private var _writer : PrintWriter = null
  setup{
    _writer = new PrintWriter(file)
  }
  shutdown{
    _writer.close
  }
  override def typedExecute(t: (CloudFoundryNodeConf, Date, MonitInfo), st : Tuple): Unit = {
    try{
      val graphName = GraphNamer.graphName(t._2)
      val sampleData = CFNodeSample(t._1.url, t._3)
      val resourceData = CFNodeResource(t._1.url, t._3)
      writeToRDFStore(graphName, sampleData.toRdf)
      writeToRDFStore("Resources", resourceData.toRdf)
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
    data.write(_writer, "N-TRIPLE")
  }
}
