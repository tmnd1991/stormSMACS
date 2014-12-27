package it.unibo.ing.utils

/**
 * @author Antonio Murgia
 * @version 24/12/14.
 */
import java.util.Date

object RDFUtils {
  def graphName(d : Date) = "<http://stormsmacs/sample/" + DateUtils.format(d,"yyyy-MM-dd_HH:mm:ss>")
}
