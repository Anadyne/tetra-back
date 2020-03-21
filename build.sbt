import Versions._

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

lazy val commonSettings = Seq(
// Refine scalac params from tpolecat
  scalacOptions --= Seq(
    "-Xfatal-warnings"
  )
)

lazy val catsDeps = libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion
)

lazy val zioDeps = libraryDependencies ++= Seq(
  "dev.zio" %% "zio"          % zioVersion,
  "dev.zio" %% "zio-test"     % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt" % zioVersion % "test"
)

lazy val root = (project in file("."))
  .settings(
    organization := "Neurodyne",
    name := "zio-top",
    version := "0.0.1",
    scalaVersion := "2.13.1",
    maxErrors := 3,
    commonSettings,
    zioDeps,
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
