package it.unibo.ing.stormsmacs.conf

import java.io.File

/**
 * @author Antonio Murgia
 * @version 18/01/15
 * Little utility to test integrity json conf files
 */
object JsonConfTest extends App{
  if (args.length < 1)
    println("usage JsonConfTest <fileToTest>")
  else{
    try {
      JsonConfiguration.readJsonConf(io.Source.fromFile(new File(args(0))).mkString)
      println("Json configuration file is Ok!")
    }
    catch{
      case t : Throwable => println(t.getMessage())
    }
  }
}
