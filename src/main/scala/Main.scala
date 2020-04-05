package org.fsf.tetra

import org.fsf.tetra.model.config.config.{ loadConfig }
import org.fsf.tetra.module.db.ExtServices
import org.fsf.tetra.module.logger.logger
import org.fsf.tetra.route.UserRoute
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.Logger

import cats.effect.{ ExitCode }
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import types._

import zio.clock.Clock
import zio.console.putStrLn
import zio.interop.catz._
import zio.{ ZIO }

object Main extends CatsApp {

  private val userRoute = new UserRoute[AppEnvironment]
  private val yaml      = userRoute.getEndPoints.toOpenAPI("User", "1.0").toYaml
  private val httpApp =
    Router("/" -> userRoute.getRoutes, "/docs" -> new SwaggerHttp4s(yaml).routes[AppTask]).orNotFound
  private val finalHttpApp = Logger.httpApp[AppTask](true, true)(httpApp)

  val env = ExtServices.liveEnv ++ logger.liveEnv ++ Clock.live

  override def run(args: List[String]) = {
    val res = for {
      cfg <- ZIO.fromEither(loadConfig())
      server = ZIO
        .runtime[AppEnvironment]
        .flatMap(implicit rts =>
          BlazeServerBuilder[AppTask]
            .bindHttp(cfg.server.port, cfg.server.host)
            .withHttpApp(CORS(finalHttpApp))
            .serve
            .compile[AppTask, AppTask, ExitCode]
            .drain
        )
    } yield server

    res
      .provideCustomLayer(env)
      .foldM(err => putStrLn(s"Execution failed with: $err") *> ZIO.succeed(1), _ => ZIO.succeed(0))
  }
}
