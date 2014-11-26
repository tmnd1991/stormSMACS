package it.unibo.ing.stormsmacs.topologies.bolts

import java.io.IOException
import java.util.Date
import backtype.storm.tuple.Tuple
import org.openstack.api.restful.keystone.v2.{TokenProvider, KeystoneTokenProvider}
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.OpenStackNode
import spray.json.DeserializationException
import spray.json.JsonParser.ParsingException
import storm.scala.dsl.Logging
import uk.co.bigbeeconsultants.http.header.MediaType

/**
 * @author Antonio Murgia
 */

class OpenStackNodeClientBolt(node : OpenStackNode)
  extends ClientBolt(List("ceilometerData"), node.connectTimeout, node.readTimeout)
  with Logging{
  private var tokenProvider : TokenProvider = _

  setup{
    tokenProvider = KeystoneTokenProvider.getInstance(node.keystoneUrl, node.tenantName, node.username, node.password)
  }

  override def emitData(t: Tuple, graphName: Date) = {
    val token = tokenProvider.token
    using anchor t emit(graphName, token)
  }
}