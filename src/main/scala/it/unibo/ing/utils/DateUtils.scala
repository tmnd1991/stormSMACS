package it.unibo.ing.utils

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by tmnd on 10/10/14.
 */
object DateUtils {
  def parseOption(stringDate : String, format : String = "yyyy-MM-dd'T'HH:mm:ss'Z'") : Option[Date] = {
    try{
      Some(parse(stringDate,format))
    }
    catch{
      case e : Throwable => None
    }
  }

  def parse(stringDate : String, format : String = "yyyy-MM-dd'T'HH:mm:ss'Z'") : Date = {
    new SimpleDateFormat(format,java.util.Locale.forLanguageTag("en")) parse stringDate
  }

  def format(date : Date, format : String = "yyyy-MM-dd'T'HH:mm:ss'Z'") : String = {
    new SimpleDateFormat(format,java.util.Locale.forLanguageTag("en")).format(date)
  }

  def formatOption(date : Date, format : String = "yyyy-MM-dd'T'HH:mm:ss'Z'") : Option[String] = {
    try{
      Some(this.format(date,format))
    }
    catch{
      case e : Throwable => None
    }
  }
}
