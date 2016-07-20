
name := "actors"

scalaVersion := "2.11.8"

version := "0.1.1"

resolvers += "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers += "Sedis Repo" at "http://pk11-scratch.googlecode.com/svn/trunk"
resolvers += "Reactive8ouchbase Snapshots" at "https://raw.github.com/ReactiveCouchbase/repository/master/snapshots/"
resolvers += "ReactiveCouchbase Releases" at "https://raw.github.com/ReactiveCouchbase/repository/master/releases/"
resolvers += "NetBeans" at "http://bits.netbeans.org/nexus/content/groups/netbeans"


//scalacOptions ++= Seq("-feature", "-Xlog-implicits")
scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-cache" % "2.4.6",
  "com.typesafe.play" %% "play-ws" % "2.4.6",
  "com.typesafe.play" %% "play-json" % "2.4.6",
  "pl.matisoft" %% "swagger-play24" % "1.4",
  "com.wordnik" %% "swagger-core" % "1.3.12",
  "com.wordnik" %% "swagger-jaxrs" % "1.3.12",
  "com.livestream" %% "scredis" % "2.0.6",
  "com.softwaremill.quicklens" %% "quicklens" % "1.4.2",
  "com.badlogicgames.ashley" % "ashley" % "1.7.0"
)

javaOptions in Test += "-Dconfig.file=" + Option(System.getProperty("config.file")).getOrElse("conf/application.conf")
lazy val commons = ProjectRef(file("../commons"), "commons")

lazy val root = (project in file(".")).dependsOn(commons).enablePlugins(PlayScala)
