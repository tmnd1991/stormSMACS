package uk.co.bigbeeconsultants.http

import java.net.URL

import uk.co.bigbeeconsultants.http.header.{CookieJar, Headers, MediaType}
import uk.co.bigbeeconsultants.http.request.{Request, RequestBody}
import uk.co.bigbeeconsultants.http.response.Response

/**
 * Created by tmnd on 11/11/14.
 */
class ModdedHttpClient(commonConfig: Config = Config()) extends HttpClient(commonConfig){
  def myGet(url: URL, mediaType : MediaType): Response = get(url, Headers(List()), "", mediaType, None)
  def get(url: URL,
          requestHeaders: Headers,
          body : String,
          mediaType : MediaType,
          cookies: Option[CookieJar]): Response = makeRequest(Request(Request.GET,
                                                                      url,
                                                                      scala.Some(RequestBody(body,mediaType)),
                                                                      requestHeaders,
                                                                      cookies))
}
