addSbtPlugin("org.scalameta"             % "sbt-scalafmt"        % "2.3.2")
addSbtPlugin("ch.epfl.scala"             % "sbt-scalafix"        % "0.9.13")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"        % "0.1.11")
addSbtPlugin("com.typesafe.sbt"          % "sbt-native-packager" % "1.7.0")
addSbtPlugin("io.spray"                  % "sbt-revolver"        % "0.9.1")

libraryDependencies += "com.spotify" % "docker-client" % "8.16.0"
