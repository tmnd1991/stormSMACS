package it.unibo.ing.stormsmacs.topologies.bolts

import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

import backtype.storm.tuple.Tuple
import spray.json.DeserializationException
import spray.json.JsonParser.ParsingException
import storm.scala.dsl.Logging
import java.util.Date
/**
 * Created by tmnd on 25/11/14.
 */
abstract class ClientBolt(outputFields : List[String], connectTimeout : Int, readTimeout : Int)
  extends HttpRequesterBolt(outputFields, connectTimeout, readTimeout)
  with Logging {
  override final def execute(t: Tuple) = {
    t matchSeq {
      case Seq(graphName: Date) => {
        try {
          emitData(t, graphName)
          t.ack
        }
        catch {
          case e : InterruptedException => {
            logger.error("int " + e.getMessage)
          }
          case e : ExecutionException => {
            logger.error("exe " + e.getMessage)
          }
          case e : TimeoutException => {
            logger.error("timeout " + e.getMessage)
          }
          case e : IOException => {
            logger.error("io " + e.getMessage)
          }
          case e: ParsingException => {
            logger.error("parsing " + e.getMessage)
          }
          case e: DeserializationException => {
            logger.error("deserialization " + e.getMessage)
          }
        }
      }
    }
  }

  def emitData(t : Tuple, graphName : Date) : Unit
}