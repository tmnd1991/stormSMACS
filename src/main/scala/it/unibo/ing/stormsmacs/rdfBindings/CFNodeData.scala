package it.unibo.ing.stormsmacs.rdfBindings

/**
 * @author Murgia Antonio
 * @version 15/12/14
 */
import java.net.URL
import com.hp.hpl.jena.graph.GraphMaker
import com.hp.hpl.jena.graph.impl.SimpleGraphMaker
import com.hp.hpl.jena.rdf.model.{Model, ModelFactory}
import com.hp.hpl.jena.vocabulary.VCARD
import it.unibo.ing.monit.model.MonitInfo
import it.unibo.ing.rdf.RdfWriter

case class CFNodeData(graphName : String, url : URL, infos : Seq[MonitInfo]) {
}
object CFNodeDataRdfConversion{
  implicit object CFNodeDataRDFWriter extends RdfWriter[CFNodeData]{
    override def write(obj: CFNodeData): Model = {
      val graph = new SimpleGraphMaker().createGraph(obj.graphName)
      val model = ModelFactory.createModelForGraph(graph)
      val r = model.createResource(obj.url.toString).addProperty(VCARD.FN, obj.infos.toString())
      model
    }
  }
}