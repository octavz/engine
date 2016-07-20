name := "commons"

scalaVersion := "2.11.8"

version := "0.1.1"

resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.4.6",
      "com.badlogicgames.ashley" % "ashley" % "1.7.2",
      "com.assembla.scala-incubator" %% "graph-json" % "1.11.0",
      "com.assembla.scala-incubator" %% "graph-core" % "1.11.0",
      "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.7.2")
