package it.unibo.ing.stormsmacs.topologies.builders

import backtype.storm.topology.TopologyBuilder
import storm.scala.dsl.TypedTopologyBuilder

/**
 * Created by tmnd91 on 08/03/15.
 */
abstract class StormSmacsBuilder {
  def build(builder : TypedTopologyBuilder) : TypedTopologyBuilder
}
