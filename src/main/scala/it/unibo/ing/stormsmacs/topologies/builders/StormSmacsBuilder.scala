package it.unibo.ing.stormsmacs.topologies.builders

import backtype.storm.topology.TopologyBuilder
import storm.scala.dsl.TypedTopologyBuilder

/**
 * @author Antonio Murgia
 * @version 08/03/15
 * Abstract builder
 */
abstract class StormSmacsBuilder {
  def build(builder : TypedTopologyBuilder) : TypedTopologyBuilder
}
