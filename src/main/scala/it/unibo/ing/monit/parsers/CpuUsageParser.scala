package it.unibo.ing.monit.parsers

import it.unibo.ing.monit.model.CpuUsage

/**
 * Created by tmnd on 03/11/14.
 */
object CpuUsageParser extends Parser[CpuUsage,Seq[String]]{
  override def parse(seq: Seq[String]) = {
    val togliPrimoEultimi3 = (s : String) => s.substring(1,s.length-3)
    CpuUsage(togliPrimoEultimi3(seq.head).toDouble, togliPrimoEultimi3(seq.tail.head).toDouble, togliPrimoEultimi3(seq.tail.tail.head).toDouble)
  }
}
