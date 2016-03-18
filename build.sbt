name := """simple-chat"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.1"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
