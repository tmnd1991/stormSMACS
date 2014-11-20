package it.unibo.ing.monit.parsers

import it.unibo.ing.monit.model.LoadAverage

/**
 * Created by tmnd on 03/11/14.
 */
object LoadAverageParser extends Parser[LoadAverage,Seq[String]]{
  override def parse(x: Seq[String]): LoadAverage ={
    val togliPrimoEultimo = (s : String) => s.substring(1,s.length-1)
    LoadAverage(togliPrimoEultimo(x.head).toDouble,togliPrimoEultimo(x.tail.head).toDouble,togliPrimoEultimo(x.tail.tail.head).toDouble)
  }
}
