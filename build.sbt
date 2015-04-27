name := "storm-smacs"

version := "1.0"

organization := "it.unibo.ing"

scalaVersion := "2.11.4"

scalacOptions := Seq("-feature", "-deprecation", "-language:postfixOps")

resolvers ++= Seq("clojars" at "http://clojars.org/repo/",
                  "clojure-releases" at "http://build.clojure.org/releases")

//STORM
libraryDependencies += "org.apache.storm" % "storm-core" % "0.9.3" % "provided" exclude("junit", "junit") withSources()

//JSON PARSING
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.1"

//SCALASTORM DSL
//libraryDependencies += "com.github.velvia" %% "scala-storm" % "2.0" withSources() intransitive()
libraryDependencies += "com.github.velvia" %% "scala-storm" % "1.0" withSources()

//SOME UTILS
libraryDependencies += "it.unibo.ing" %% "utils" % "1.0" withSources() intransitive()

//CEILOMETER JSON WRAPPER
//libraryDependencies += "it.unibo.ing.smacs" %% "ceilometerapi4s" % "0.7" withSources() intransitive()
libraryDependencies += "it.unibo.ing.smacs" %% "ceilometerapi4s" % "0.8" withSources() intransitive()

//HTTP CLIENT
//libraryDependencies += "org.eclipse.jetty" % "jetty-client" % "9.3.0.M1"
libraryDependencies += "org.eclipse.jetty" % "jetty-client" % "8.1.16.v20140903" withJavadoc()

//TESTING
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.2"

//rdf library
libraryDependencies += "org.apache.jena" % "jena-core" % "2.11.2" exclude("org.slf4j","slf4j-api") exclude("junit", "junit") intransitive()

//sparql queryengine
libraryDependencies += "org.apache.jena" % "jena-arq" % "2.11.2" exclude("org.slf4j","jcl-over-slf4j") exclude("org.apache.commons", "commons-csv")

//serializer for scala classes
libraryDependencies += ("com.twitter" % "chill_2.11" % "0.5.2").exclude("com.esotericsoftware.minlog", "minlog")



