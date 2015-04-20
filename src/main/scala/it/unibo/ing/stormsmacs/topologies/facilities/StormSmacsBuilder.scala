package it.unibo.ing.stormsmacs.topologies.facilities

import backtype.storm.topology.TopologyBuilder

/**
 * @author Antonio Murgia
 * @version 08/03/15
 * Abstract builder
 */
abstract class StormSmacsBuilder {
  def build(builder : TopologyBuilder) : TopologyBuilder
}