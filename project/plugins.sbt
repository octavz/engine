addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "SBT repository" at "http://repo.typesafe.com/typesafe/simple/maven-releases/"

resolvers += "Third party repository" at "http://repo.typesafe.com/typesafe/simple/third-party/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "4.0.0")


logLevel := Level.Warn
