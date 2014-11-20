package it.unibo.ing.stormsmacs.topologies.spouts

import it.unibo.ing.stormsmacs.conf.OpenStackNode
import storm.scala.dsl.StormSpout

/**
 * Created by tmnd on 18/11/14.
 */
class OpenStackNodeSpout(node : OpenStackNode, pollTime : Int) extends StormSpout(List()){
  override def nextTuple() = {
    emit(Nil)
  }
}
