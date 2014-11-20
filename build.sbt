name := "storm-smacs"

version := "1.0"

organization := "it.unibo.ing"

scalacOptions := Seq("-feature")

resolvers ++= Seq("clojars" at "http://clojars.org/repo/",
                  "clojure-releases" at "http://build.clojure.org/releases")

resolvers += "Big Bee Consultants" at "http://repo.bigbeeconsultants.co.uk/repo"

//JSON PARSING
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.1"

//SCALASTORM DSL
libraryDependencies += "com.github.velvia" %% "scala-storm" % "1.0"

//LOGGING
libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.+",
                         "ch.qos.logback" % "logback-core"    % "1.0.+",
                         "ch.qos.logback" % "logback-classic" % "1.0.+")

//STORM
libraryDependencies += "org.apache.storm" % "storm-core" % "0.9.2-incubating" % "provided" exclude("junit", "junit")

//HTTPCLIENT
libraryDependencies += "uk.co.bigbeeconsultants" %% "bee-client" % "0.21.+"