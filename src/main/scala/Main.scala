package org.fsf.tetra

import scala.util.Try

import cats.effect.ExitCode
import org.fsf.tetra.model.config.Application
import org.fsf.tetra.module.db.userRepository._
import org.fsf.tetra.route.UserRoute
import eu.timepit.refined.auto._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import zio.{ RIO, ZEnv, ZIO }
import zio.clock.Clock
import zio.console.putStrLn
import zio.interop.catz._

import org.http4s.HttpApp
import org.http4s.server.middleware.CORS

object Main extends App {

  type AppEnvironment = Clock with UserRepository
  type AppTask[A]     = RIO[AppEnvironment, A]

  private val userRoute = new UserRoute[AppEnvironment]
  private val yaml      = userRoute.getEndPoints.toOpenAPI("User", "1.0").toYaml
  private val httpApp =
    Router("/" -> userRoute.getRoutes, "/docs" -> new SwaggerHttp4s(yaml).routes[AppTask]).orNotFound
  private val finalHttpApp = Logger.httpApp[AppTask](true, true)(httpApp)

  val env = ZEnv.live ++ UserRepository.live

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val res = for {
      cfg     <- ZIO.fromTry(Try(Application.getConfig))
      prog    = runHttp(finalHttpApp, cfg)
      appCfg  = Application.appConfig(cfg)
      program <- prog.provideLayer(env)
    } yield program

    res.foldM(err => putStrLn(s"Execution failed with: $err").as(1), _ => ZIO.succeed(0))
  }

  def runHttp[R <: Clock](app: HttpApp[AppTask], cfg: Application) = ZIO.runtime[AppEnvironment].flatMap {
    implicit rts =>
      BlazeServerBuilder[AppTask]
        .bindHttp(cfg.server.port, cfg.server.host.getHostAddress)
        .withHttpApp(CORS(app))
        .serve
        .compile[AppTask, AppTask, ExitCode]
        .drain
  }
}
