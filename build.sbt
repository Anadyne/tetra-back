import BuildHelper._

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

lazy val zioDeps = libraryDependencies ++= Seq(
  "dev.zio" %% "zio"              % Version.zio,
  "dev.zio" %% "zio-test"         % Version.zio % "test",
  "dev.zio" %% "zio-test-sbt"     % Version.zio % "test",
  "dev.zio" %% "zio-interop-cats" % Version.zioInteropCats
)

lazy val http4sDeps = libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-core"         % Version.http4s,
  "org.http4s" %% "http4s-blaze-server" % Version.http4s
)

lazy val tapirDeps = libraryDependencies ++= Seq(
  "com.softwaremill.sttp.tapir" %% "tapir-core"               % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-sttp-stub-server"   % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % Version.tapir
)

lazy val sttpDeps = libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client" %% "core"                          % Version.sttp,
  "com.softwaremill.sttp.client" %% "circe"                         % Version.sttp,
  "com.softwaremill.sttp.client" %% "async-http-client-backend-zio" % Version.sttp
)

lazy val commonDeps = libraryDependencies ++= Seq(
  "io.circe"              %% "circe-generic"  % Version.circe,
  "io.getquill"           %% "quill-jdbc"     % Version.quill,
  "com.github.pureconfig" %% "pureconfig"     % Version.pureconfig,
  "com.h2database"        % "h2"              % Version.h2db,
  "org.flywaydb"          % "flyway-core"     % Version.flyway,
  "ch.qos.logback"        % "logback-classic" % Version.logback
)

lazy val root = (project in file("."))
  .settings(
    organization := "Anadyne",
    name := "tetra-back",
    version := "0.0.1",
    scalaVersion := "2.13.3",
    maxErrors := 5,
    commonSettings,
    commonDeps,
    zioDeps,
    http4sDeps,
    tapirDeps,
    sttpDeps,
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
  )

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")

scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % "0.5.4"
