val sprayVersion = "1.3.+"

val slf4jVersion = "1.7.12"

lazy val server = (project in file(".")).enablePlugins(JavaServerAppPackaging).settings (

  name := "apollo",

  organization := "com.sungevity.analytics",

  version := "1.0.0-SNAPSHOT",

  scalaVersion := "2.11.6",

  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",

  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.0",
    "io.spray" %% "spray-http" % sprayVersion,
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-client" % sprayVersion,
    "io.spray" %% "spray-json" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "org.slf4j" % "slf4j-api" % slf4jVersion,
    "org.slf4j" % "slf4j-log4j12" % slf4jVersion,
    "com.typesafe.akka" %% "akka-actor" % "2.3.14",
    "com.enragedginger" %% "akka-quartz-scheduler" % "1.4.0-akka-2.3.x",
    "joda-time" % "joda-time" % "2.8.1",
    "com.github.nscala-time" %% "nscala-time" % "2.0.0",
    "org.apache.commons" % "commons-lang3" % "3.4",
    "org.scalaz" %% "scalaz-core" % "7.1.3",
    "com.sungevity.analytics" %% "apollo-toolkit" % "1.0.+",
    "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
  ),

  mainClass := Some("com.sungevity.analytics.Main")

)
