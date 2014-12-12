package it.unibo.ing.stormsmacs.topologies.bolts

import backtype.storm.tuple.Tuple
import java.util.Date
import it.unibo.ing.stormsmacs.conf.FusekiNode
import org.openstack.api.restful.ceilometer.v2.elements.Statistics
import storm.scala.dsl.{Logging, StormBolt}

/**
 * @author Antonio Murgia
 * @version 12/12/2014
 */
class OpenStackNodePersisterBolt(fusekiEndpoint : FusekiNode) extends StormBolt(List()) with Logging {
  override def execute(t: Tuple) = {
    t matchSeq{
      case Seq(nodeName : String, d : Date, meterName : String, stat : Statistics) => {

      }
    }
    t.ack
  }
}
