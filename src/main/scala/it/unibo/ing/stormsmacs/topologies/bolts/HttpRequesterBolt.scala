package it.unibo.ing.stormsmacs.topologies.bolts

import storm.scala.dsl.{StormBolt}
import uk.co.bigbeeconsultants.http.{Config, ModdedHttpClient}

/**
 * Created by tmnd on 24/11/14.
 */
abstract class HttpRequesterBolt (outputFields: List[String],
                         connectTimeout : Int,
                         readTimeout : Int,
                         followRedirects : Boolean = false) extends StormBolt(outputFields) {
  protected var httpClient: ModdedHttpClient = _
  setup {
    httpClient = new ModdedHttpClient(Config(connectTimeout = connectTimeout,
      readTimeout = readTimeout,
      followRedirects = false)
    )
  }
}