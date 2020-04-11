package org.fsf.tetra.route

import io.circe.generic.auto._

import org.fsf.tetra.implicits.Throwable._
import org.fsf.tetra.model.database.User
import org.fsf.tetra.model.response.{ BadRequestResponse, ErrorResponse, InternalServerErrorResponse, NotFoundResponse }
import org.fsf.tetra.model.{ DBFailure, ExpectedFailure, NotFoundFailure }
import org.fsf.tetra.module.db.ExtServices.UserRepository
import org.fsf.tetra.module.logger.logger.Logger
import org.http4s._
import org.http4s.dsl.Http4sDsl

import cats.syntax.semigroupk._
import sttp.tapir.DecodeResult.Error
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerDefaults.StatusCodes
import sttp.tapir.server.http4s._
import sttp.tapir.server.{ DecodeFailureContext, ServerDefaults }
import sttp.tapir.server.{ DecodeFailureHandling }

import zio.console.{ putStrLn }
import zio.interop.catz._
import zio.{ RIO, ZIO }

object Endpoints {

  val getUserEndPoint = endpoint.get
    .in("user" / path[Long]("user id"))
    .errorOut(
      oneOf(
        statusMapping(StatusCodes.error, jsonBody[InternalServerErrorResponse]),
        statusMapping(StatusCodes.error, jsonBody[NotFoundResponse])
      )
    )
    .out(jsonBody[User])

  val createUserEndPoint = endpoint.post
    .in("user")
    .in(jsonBody[User])
    .errorOut(
      oneOf[ErrorResponse](
        statusMapping(StatusCodes.error, jsonBody[InternalServerErrorResponse])
      )
    )
    .out(statusCode(StatusCodes.success))

  val deleteUserEndPoint = endpoint.delete
    .in("user" / path[Long]("user id"))
    .errorOut(
      oneOf(
        statusMapping(StatusCodes.error, jsonBody[InternalServerErrorResponse]),
        statusMapping(StatusCodes.error, jsonBody[NotFoundResponse])
      )
    )
    .out(emptyOutput)
}

class UserRoute[R <: UserRepository with Logger] extends Http4sDsl[RIO[R, *]] {
  import Endpoints._

  private implicit val customServerOptions: Http4sServerOptions[RIO[R, *]] = Http4sServerOptions
    .default[RIO[R, *]]
    .copy(
      decodeFailureHandler = (ctx: DecodeFailureContext) => {
        ctx.failure match {
          case Error(_, error) =>
            DecodeFailureHandling.response(jsonBody[BadRequestResponse])(BadRequestResponse(error.toString))
          case _ => ServerDefaults.decodeFailureHandler(ctx)
        }
      }
    )

  val newRoute: HttpRoutes[RIO[R, *]] = createUserEndPoint.toRoutes { user =>
    putStrLn(s">>>>>> New user $user")
    println(s">>>>>> New user $user")
    handleError(UserRepository.create(user))
  }

  val getRoute: HttpRoutes[RIO[R, *]] = getUserEndPoint.toRoutes(userId => handleError(getUser(userId)))

  val delRoute: HttpRoutes[RIO[R, *]] = deleteUserEndPoint.toRoutes { id =>
    val result = for {
      _ <- Logger.debug(s"id: $id")
      // user <- UserRepository.getUser(id)
      _ <- UserRepository.delete(0)
    } yield {}

    handleError(result)
  }

  val allRoutes: HttpRoutes[RIO[R, *]] = { newRoute <+> getRoute <+> delRoute }

  val getEndPoints = {
    List(getUserEndPoint, createUserEndPoint, deleteUserEndPoint)
  }

  private def getUser(userId: Long): ZIO[R, ExpectedFailure, User] =
    for {
      _    <- Logger.debug(s"id: $userId")
      user <- UserRepository.get(userId)
      u <- user match {
            case None    => ZIO.fail(NotFoundFailure(s"Can not find a user by $userId"))
            case Some(s) => ZIO.succeed(s)
          }
    } yield {
      u
    }

  private def handleError[A](result: ZIO[R, ExpectedFailure, A]): ZIO[R, Throwable, Either[ErrorResponse, A]] =
    result
      .fold(
        {
          case DBFailure(t)             => Left(InternalServerErrorResponse("Database BOOM !!!", t.getMessage, t.getStacktrace))
          case NotFoundFailure(message) => Left(NotFoundResponse(message))
        },
        Right(_)
      )
      .foldCause(
        c => Left(InternalServerErrorResponse("Unexpected errors", "", c.squash.getStacktrace)),
        identity
      )

}
