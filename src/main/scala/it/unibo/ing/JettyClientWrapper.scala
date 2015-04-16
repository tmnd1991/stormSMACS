package it.unibo.ing

import java.net.URI

import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.client.ContentExchange

/**
 * Created by tmnd91 on 17/04/15.
 */
class JettyClientWrapper(client : HttpClient) {
  private val _client = client
  def this() = this(new HttpClient())
  def doGET(uri : URI, timeout : Long = 5000) : JettyHttpResponse = {
    val exchange = new ContentExchange()
    exchange.setURI(uri)
    exchange.setMethod("GET")
    exchange.setTimeout(timeout)
    _client.send(exchange)
    val state = exchange.waitForDone()
    val body = exchange.getResponseContent
    JettyHttpResponse(exchange.getResponseStatus, exchange.getResponseContent())
  }
  def stop() = _client.stop()
}
case class JettyHttpResponse(code : Int, content : String){
  def isSuccess = (code >= 200 && code < 300)
}
