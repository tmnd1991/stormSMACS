/**
 * Created by tmnd on 25/11/14.
 */

import java.io.File

import it.unibo.ing.stormsmacs.conf.JsonConfiguration
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeData
import java.net.URL
import org.scalatest._
import it.unibo.ing.rdf._
import it.unibo.ing.stormsmacs.rdfBindings.CFNodeDataRdfConversion._
class MainTest extends FlatSpec with Matchers{
  val jsonConfFile = "/confExample.json"
  val jsonText = io.Source.fromFile(new File(jsonConfFile)).mkString
  val conf = JsonConfiguration.readJsonConf(jsonText)
  val node = CFNodeData("pippo", new URL("http://192.168.1.10"), Seq())
  node.toRdf.write(System.out)
}

/**
 * LOAD  INTO GRAPH <http://graphURI>
 */
