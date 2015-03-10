package it.unibo.ing.stormsmacs

import java.net.{URL, URI}
import java.util.Date
import it.unibo.ing.utils._

/**
 * @author Antonio Murgia
 * @version 09/01/15
 * Object to centralize the assignment of graph names
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
  def cleanURL(u : URL) : URL = {
    new URL(u.getProtocol + "://" +u.getHost)
  }
}
