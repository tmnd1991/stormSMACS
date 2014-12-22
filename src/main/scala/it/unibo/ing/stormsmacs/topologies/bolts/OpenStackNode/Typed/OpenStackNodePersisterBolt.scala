package it.unibo.ing.stormsmacs.topologies.bolts.OpenStackNode.Typed

import java.util.Date

import it.unibo.ing.stormsmacs.conf.OpenStackNodeConf
import org.openstack.api.restful.ceilometer.v2.elements.Statistics
import storm.scala.dsl.{NonEmittingTypedBolt, TypedBolt}

/**
 * Created by tmnd91 on 22/12/14.
 */
class OpenStackNodePersisterBolt extends NonEmittingTypedBolt[(OpenStackNodeConf, Date, String, Statistics)] {
  override def typedExecute(t: (OpenStackNodeConf, Date, String, Statistics)) = {
    //t._1 cici
  }
}
