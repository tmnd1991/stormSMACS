package it.unibo.ing.stormsmacs.topologies.bolts

import java.io.IOException

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
  with Logging
{
  override final def execute(t : Tuple) = t matchSeq {
    case Seq(graphName: Date) => {
      try{
        emitData(t, graphName)
      }
      catch{
        case e : IOException => {
          logger.error(e.getMessage)
        }
        case e : ParsingException => {
          logger.error(e.getMessage)
        }
        case e : DeserializationException => {
          logger.error(e.getMessage)
        }
      }
    }
      t.ack
  }
  def emitData(t : Tuple, graphName : Date) : Unit
}
