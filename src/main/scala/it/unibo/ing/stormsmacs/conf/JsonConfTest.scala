package it.unibo.ing.stormsmacs.conf

import java.io.File

/**
 * Created by tmnd91 on 18/01/15.
 */
object JsonConfTest extends App{
  override def main(args : Array[String]): Unit ={
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

}
