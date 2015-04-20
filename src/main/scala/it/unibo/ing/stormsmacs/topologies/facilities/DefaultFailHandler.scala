package it.unibo.ing.stormsmacs.topologies.facilities

/**
 * Created by tmnd91 on 19/04/15.
 */
class DefaultFailHandler(numRetries: Int = 2) extends FailHandler[Long]{
  private var _retries = Map[Long,Int]()
  override def shouldBeReplayed(messageId: Long): Boolean = {
    val nRetries = _retries.get(messageId) match{
      case Some(i: Int) => i
      case None =>
        _retries += (messageId ->  0)
        0
    }
    nRetries < numRetries
  }
  override def acked(messageId: Long) : Unit = _retries -= messageId
  override def replayed(messageId: Long) : Unit = _retries += (messageId -> (_retries(messageId) + 1))
  override def failCount(messageId: Long): Long = _retries.get(messageId) match{
    case Some(i: Int) => i
    case None => 0
  }
}
