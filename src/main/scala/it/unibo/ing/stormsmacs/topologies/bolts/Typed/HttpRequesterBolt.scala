package it.unibo.ing.stormsmacs.topologies.bolts.Typed

import it.unibo.ing.JettyClientWrapper
import org.eclipse.jetty.client.HttpClient
import storm.scala.dsl.StormBolt

/**
 * @author Antonio Murgia
 * @version 24/12/2014
 * Typed bolt with a jetty HttpClient embedded
 */
abstract class HttpRequesterBolt (connectTimeout : Int,
                                      readTimeout : Int,
                                      followRedirects : Boolean,
                                      outputFields: String*)
  extends StormBolt(outputFields.toList) {

  protected var httpClient : JettyClientWrapper = _
  setup {
    val c = new HttpClient()
    c.setConnectTimeout(connectTimeout)
    c.setMaxRedirects(1)
    c.start()
    httpClient = new JettyClientWrapper(c)
  }

  shutdown{
    httpClient.stop()
    httpClient = null //let's help the GC :D
  }
}