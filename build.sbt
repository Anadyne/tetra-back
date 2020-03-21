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

lazy val zioDeps = libraryDependencies ++= Seq(
  "dev.zio" %% "zio"              % Versions.zio,
  "dev.zio" %% "zio-test"         % Versions.zio % "test",
  "dev.zio" %% "zio-test-sbt"     % Versions.zio % "test",
  "dev.zio" %% "zio-interop-cats" % Versions.zioInteropCats
)

lazy val commonDeps = libraryDependencies ++= Seq(
  "org.http4s"                  %% "http4s-core"              % Versions.http4s,
  "org.http4s"                  %% "http4s-dsl"               % Versions.http4s,
  "org.http4s"                  %% "http4s-blaze-server"      % Versions.http4s,
  "org.http4s"                  %% "http4s-circe"             % Versions.http4s,
  "io.circe"                    %% "circe-generic"            % Versions.circe,
  "io.getquill"                 %% "quill-jdbc"               % Versions.quill,
  "is.cir"                      %% "ciris-cats"               % Versions.ciris,
  "is.cir"                      %% "ciris-cats-effect"        % Versions.ciris,
  "is.cir"                      %% "ciris-core"               % Versions.ciris,
  "is.cir"                      %% "ciris-enumeratum"         % Versions.ciris,
  "is.cir"                      %% "ciris-generic"            % Versions.ciris,
  "is.cir"                      %% "ciris-refined"            % Versions.ciris,
  "com.softwaremill.sttp.tapir" %% "tapir-core"               % Versions.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % Versions.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % Versions.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Versions.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Versions.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Versions.tapir,
  "com.h2database"              % "h2"                        % Versions.h2database,
  "ch.qos.logback"              % "logback-classic"           % Versions.logback
)

lazy val root = (project in file("."))
  .settings(
    organization := "FSF",
    name := "tetra-back",
    version := "0.0.1",
    scalaVersion := "2.13.1",
    maxErrors := 5,
    commonSettings,
    zioDeps,
    commonDeps,
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

addCompilerPlugin("org.typelevel" %% "kind-projector" % Versions.kindProjector cross CrossVersion.full)

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
