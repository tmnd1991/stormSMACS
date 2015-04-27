package it.unibo.ing.stormsmacs.topologies.unreliable.bolts.Generic

import java.net.URI
import java.util.Date

import backtype.storm.tuple.Tuple
import it.unibo.ing.sigar.restful.model.SigarMeteredData
import it.unibo.ing.sigar.restful.model.SigarMeteredDataFormat._
import it.unibo.ing.stormsmacs.conf.GenericNodeConf
import it.unibo.ing.stormsmacs.topologies.facilities.HttpRequesterBolt
import it.unibo.ing.utils._
import spray.json._
import storm.scala.dsl.additions.Logging

/**
  * @author Antonio Murgia
  * @version 18/11/2014
  * Storm Bolt that gets Sample Data from given node
  */
class GenericClientBolt(val node : GenericNodeConf, val pollTime: Long)
  extends HttpRequesterBolt(node.connectTimeout, node.readTimeout, false, "Node","GraphName","MonitData")
     with Logging
   {
     require (pollTime > 0)
     override def execute(t: Tuple) : Unit = t matchSeq {
       case Seq(date: Date) =>{
         val url: URI = node.url.toURI / (date.getTime - pollTime).toString / date.getTime.toString
         try{
           val data = httpClient.doGET(url , node.readTimeout)
           if (data.isSuccess){
             import spray.json.DefaultJsonProtocol._
             val convertedData = data.content.parseJson.convertTo[Seq[SigarMeteredData]]
             for (d <- convertedData)
               using no anchor emit (node, date, d)
             logger.info("ack " + date)
           }
           else{
             logger error (url + ": response code not successful")
             logger info ("fail " + date)
           }

         }
         catch{
           case e : Throwable =>
             logger.error(e.getMessage,e)
             logger info ("fail " + date)
         }

       }
     }
   }