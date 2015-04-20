package it.unibo.ing.stormsmacs.topologies.facilities

/**
 * Created by tmnd91 on 19/04/15.
 */
trait FailHandler[T] {
  def shouldBeReplayed(messageId: T) : Boolean
  def acked(messageId: T) : Unit
  def failCount(messageId: T) : Long
  def replayed(messageId: T) : Unit
}
