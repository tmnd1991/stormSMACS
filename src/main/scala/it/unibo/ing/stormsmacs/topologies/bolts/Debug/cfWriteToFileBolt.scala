package it.unibo.ing.stormsmacs.topologies.bolts.Debug

import java.io.{PrintWriter, OutputStreamWriter, BufferedWriter, File}
import java.util.Date

import backtype.storm.tuple.Tuple
import com.hp.hpl.jena.rdf.model.Model
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.stormsmacs.GraphNamer
import it.unibo.ing.stormsmacs.conf.CloudFoundryNodeConf
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeData
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfConversion._
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
      val data = CFNodeData(t._1.url, t._3)
      val model = data.toRdf
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
    data.write(_writer, "N-TRIPLE")
  }
}
