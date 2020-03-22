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
  "dev.zio" %% "zio"              % Version.zio,
  "dev.zio" %% "zio-test"         % Version.zio % "test",
  "dev.zio" %% "zio-test-sbt"     % Version.zio % "test",
  "dev.zio" %% "zio-interop-cats" % Version.zioInteropCats
)

lazy val commonDeps = libraryDependencies ++= Seq(
  "org.http4s"             %% "http4s-core"              % Version.http4s,
  "org.http4s"             %% "http4s-dsl"               % Version.http4s,
  "org.http4s"             %% "http4s-blaze-server"      % Version.http4s,
  "org.http4s"             %% "http4s-circe"             % Version.http4s,
  "io.circe"               %% "circe-generic"            % Version.circe,
  "io.getquill"            %% "quill-jdbc"               % Version.quill,
  "is.cir"                 %% "ciris-cats"               % Version.ciris,
  "is.cir"                 %% "ciris-cats-effect"        % Version.ciris,
  "is.cir"                 %% "ciris-core"               % Version.ciris,
  "is.cir"                 %% "ciris-enumeratum"         % Version.ciris,
  "is.cir"                 %% "ciris-generic"            % Version.ciris,
  "is.cir"                 %% "ciris-refined"            % Version.ciris,
  "com.softwaremill.tapir" %% "tapir-core"               % Version.tapir,
  "com.softwaremill.tapir" %% "tapir-http4s-server"      % Version.tapir,
  "com.softwaremill.tapir" %% "tapir-swagger-ui-http4s"  % Version.tapir,
  "com.softwaremill.tapir" %% "tapir-openapi-docs"       % Version.tapir,
  "com.softwaremill.tapir" %% "tapir-openapi-circe-yaml" % Version.tapir,
  "com.softwaremill.tapir" %% "tapir-json-circe"         % Version.tapir,
  "com.h2database"         % "h2"                        % Version.h2database,
  "ch.qos.logback"         % "logback-classic"           % Version.logback
)
lazy val silencerDeps = libraryDependencies ++= Seq(
  compilerPlugin("com.github.ghik" % "silencer-plugin" % Version.silencerVersion cross CrossVersion.full),
  "com.github.ghik" % "silencer-lib" % Version.silencerVersion % Provided cross CrossVersion.full
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
    silencerDeps,
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % Version.kindProjector cross CrossVersion.full)
  )

// Aliases
addCommandAlias("rel", "reload")
addCommandAlias("com", "all compile test:compile it:compile")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fmt", "all scalafmtSbt scalafmtAll")
