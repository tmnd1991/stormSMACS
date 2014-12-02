package it.unibo.ing.stormsmacs.topologies.bolts

import org.eclipse.jetty.client.HttpClient
import storm.scala.dsl.{StormBolt}

/**
 * Created by tmnd on 24/11/14.
 */
abstract class HttpRequesterBolt (outputFields: List[String],
                         connectTimeout : Int,
                         readTimeout : Int,
                         followRedirects : Boolean = false) extends StormBolt(outputFields) {
  protected var httpClient : HttpClient = _
  setup {
    httpClient = new HttpClient()
    httpClient.setConnectTimeout(connectTimeout)
    httpClient.setFollowRedirects(false)
    httpClient.setStopTimeout(readTimeout)
    httpClient.start()
  }

  shutdown{
    httpClient.stop()
    httpClient = null //let's help the GC :D
  }
}