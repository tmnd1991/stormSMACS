package it.unibo.ing.stormsmacs.topologies.bolts.GenericNode.Typed

import java.util.Date

import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.stormsmacs.conf.{FusekiNodeConf, GenericNodeConf}
import storm.scala.dsl.NonEmittingTypedBolt

/**
 * Created by tmnd91 on 24/12/14.
 */
class GenericNodePersisterBolt(fusekiEndpoint : FusekiNodeConf) extends NonEmittingTypedBolt[(GenericNodeConf, Date, SigarMeteredData)]{
  override def typedExecute(t: (GenericNodeConf, Date, SigarMeteredData)): Unit = {
    //qualcosa con virtuoso per fare il persist
  }
}