/**
 * Created by tmnd on 25/11/14.
 */

import java.io.File

import it.unibo.ing.stormsmacs.conf.JsonConfiguration
import org.scalatest._

class MainTest extends FlatSpec with Matchers{
  val jsonConfFile = "/confExample.json"
  val jsonText = io.Source.fromFile(new File(jsonConfFile)).mkString
  val conf = JsonConfiguration.readJsonConf(jsonText)

}
