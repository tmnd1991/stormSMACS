package it.unibo.ing.stormsmacs.topologies.bolts

import backtype.storm.tuple.Tuple
import java.util.Date
import it.unibo.ing.stormsmacs.conf.{OpenStackNodeConf, FusekiNodeConf}
import org.openstack.api.restful.ceilometer.v2.elements.Statistics
import storm.scala.dsl.{Logging, StormBolt}

/**
 * @author Antonio Murgia
 * @version 12/12/2014
 */
class OpenStackNodePersisterBolt(fusekiEndpoint : FusekiNodeConf) extends StormBolt(List()) with Logging {
  override def execute(t: Tuple) = {
    t matchSeq{
      case Seq(node : OpenStackNodeConf,
               d : Date,
               meterName : String,
               stat : Statistics) => {
        //FA 1 query sparql e da ack o fallisce e non da ack
        t.ack
      }
    }
  }
}
