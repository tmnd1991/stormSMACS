package it.unibo.ing.stormsmacs.topologies.bolts.Typed

import org.eclipse.jetty.client.HttpClient
import storm.scala.dsl.{TypedBolt, StormBolt}

/**
 * Created by tmnd91 on 24/12/14.
 */
abstract class HttpRequesterBolt[I<:Product,O<:Product] (connectTimeout : Int,
                                      readTimeout : Int,
                                      followRedirects : Boolean,
                                      outputFields: String*)
  extends TypedBolt[I,O](outputFields:_*) {

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