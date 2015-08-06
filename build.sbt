
name := "actors"

scalaVersion := "2.11.7"

version := "0.1.1"

resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sedis Repo" at "http://pk11-scratch.googlecode.com/svn/trunk"

resolvers += "ReactiveCouchbase Snapshots" at "https://raw.github.com/ReactiveCouchbase/repository/master/snapshots/"

resolvers += "ReactiveCouchbase Releases" at "https://raw.github.com/ReactiveCouchbase/repository/master/releases/"

//scalacOptions ++= Seq("-feature", "-Xlog-implicits")

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-cache" % "2.4.1",
  "com.typesafe.play" %% "play-ws" % "2.4.1",
  "com.typesafe.play" %% "play-json" % "2.4.1",
  "org.mockito" % "mockito-all" % "1.9.5",
  "org.reactivecouchbase" %% "reactivecouchbase-core" % "0.3",
  "pl.matisoft" %% "swagger-play24" % "1.4",
  "com.wordnik" %% "swagger-core" % "1.3.12",
  "com.wordnik" %% "swagger-jaxrs" % "1.3.12",
  "com.livestream" %% "scredis" % "2.0.6",
  "com.assembla.scala-incubator" %% "graph-test" % "1.9.0",
  "com.assembla.scala-incubator" %% "graph-json" % "1.9.2",
  "com.assembla.scala-incubator" %% "graph-core" % "1.9.3"
)

javaOptions in Test += "-Dconfig.file=" + Option(System.getProperty("config.file")).getOrElse("conf/application.conf")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

