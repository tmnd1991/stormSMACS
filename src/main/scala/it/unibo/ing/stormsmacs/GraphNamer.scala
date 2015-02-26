package it.unibo.ing.stormsmacs

import java.net.URI
import java.util.Date
import it.unibo.ing.utils._

/**
 * Created by tmnd91 on 09/01/15.
 */
object GraphNamer {
  def graphName(d : Date)(implicit base : URI = new URI("http://stormsmacs/tests/")) = {
    val uri = base / DateUtils.format(d,"yyyy-MM-dd'T'HH:mm:ss'Z'")
    "<" + uri.toString + ">"
  }
  def resourcesGraphName(implicit base : URI = new URI("http://stormsmacs/tests/")) = {
    val uri = base / "Resources"
    "<" + uri.toString + ">"
  }
}
