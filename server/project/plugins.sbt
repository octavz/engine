resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "SBT repository" at "http://repo.typesafe.com/typesafe/simple/maven-releases/"

resolvers += "Third party repository" at "http://repo.typesafe.com/typesafe/simple/third-party/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")

logLevel := Level.Warn
