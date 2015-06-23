import PlayKeys._

name := "Actors"

scalaVersion := "2.11.2"

version := "0.1.1"

resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sedis Repo" at "http://pk11-scratch.googlecode.com/svn/trunk"

resolvers += "ReactiveCouchbase Snapshots" at "https://raw.github.com/ReactiveCouchbase/repository/master/snapshots/"

resolvers += "ReactiveCouchbase Releases" at "https://raw.github.com/ReactiveCouchbase/repository/master/releases/"

//scalacOptions ++= Seq("-feature", "-Xlog-implicits")

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-cache" % "2.3.3",
  "com.typesafe.play" %% "play-ws" % "2.3.3",
  "org.mockito" % "mockito-all" % "1.9.5",
  "com.wordnik" %% "swagger-play2" % "1.3.10",
  "org.reactivecouchbase" %% "reactivecouchbase-core" % "0.3",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.2",
  "com.livestream" %% "scredis" % "2.0.6"
)

javaOptions in Test += "-Dconfig.file=" + Option(System.getProperty("config.file")).getOrElse("conf/application.conf")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

