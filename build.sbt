name := "storm-smacs"

version := "1.0"

organization := "it.unibo.ing"

scalaVersion := "2.11.2"

scalacOptions := Seq("-feature")

resolvers ++= Seq("clojars" at "http://clojars.org/repo/",
                  "clojure-releases" at "http://build.clojure.org/releases")

//STORM
libraryDependencies += "org.apache.storm" % "storm-core" % "0.9.2-incubating" % "provided" exclude("junit", "junit") withSources()

//JSON PARSING
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.1"

//SCALASTORM DSL
libraryDependencies += "com.github.velvia" %% "scala-storm" % "1.0"

//LOGGING
//libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.+",
//                         "ch.qos.logback" % "logback-core"    % "1.0.+",
//                         "ch.qos.logback" % "logback-classic" % "1.0.+")

//CEILOMETER JSON WRAPPER
libraryDependencies += "it.unibo.ing.smacs" %% "ceilometerapiwrapper" % "0.4" withSources()

//HTTP CLIENT
libraryDependencies += "org.eclipse.jetty" % "jetty-client" % "9.3.0.M1"

//TESTING
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.2"

//rdf library
libraryDependencies += "org.apache.jena" % "jena-core" % "2.12.1"