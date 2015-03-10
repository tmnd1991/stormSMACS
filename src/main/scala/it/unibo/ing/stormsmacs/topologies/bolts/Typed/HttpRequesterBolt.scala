package it.unibo.ing.stormsmacs.topologies.bolts.Typed

import org.eclipse.jetty.client.HttpClient
import storm.scala.dsl.{TypedBolt, StormBolt}

/**
 * @author Antonio Murgia
 * @version 24/12/2014
 * Typed bolt with a jetty HttpClient embedded
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
    httpClient.setMaxRedirects(1)
    httpClient.start()
  }

  shutdown{
    httpClient.stop()
    httpClient = null //let's help the GC :D
  }
}