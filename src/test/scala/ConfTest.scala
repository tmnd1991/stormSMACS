import java.io.File
import java.net.URL

import it.unibo.ing.stormsmacs.conf.JsonConfiguration
import org.scalatest.{Matchers, FlatSpec}

import scala.io.{BufferedSource, Source}

/**
 * Created by tmnd91 on 18/01/15.
 */
class ConfTest extends FlatSpec with Matchers{
  "json conf file " should " be valid " in{
    val jsonText = Source.fromURL(getClass.getResource("/confExample.json")).mkString
    val conf = JsonConfiguration.tryReadJsonConf(jsonText)
    conf should not be (None)
  }

}
