package it.unibo.ing.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * Created by tmnd on 22/10/14.
 */
object TimestampUtils {
  def parse(stringTimestamp : String) = Timestamp.valueOf(stringTimestamp.replaceAll("T", " "))
  def parseOption(stringTimestamp : String) = try{
    Some(parse(stringTimestamp))
  }
  catch{
    case e : Throwable => None
  }
  def format(t : Timestamp) = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.").format(t)+t.getNanos/1000
}
