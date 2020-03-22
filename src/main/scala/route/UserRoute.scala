package org.fsf.tetra.route

import org.fsf.tetra.implicits.Throwable._
import org.fsf.tetra.model.database.User
import org.fsf.tetra.model.response.{ BadRequestResponse, ErrorResponse, InternalServerErrorResponse, NotFoundResponse }
import org.fsf.tetra.model.{ DBFailure, ExpectedFailure, NotFoundFailure }
import org.fsf.tetra.module.db.userRepository.UserRepository
import org.http4s._

import cats.syntax.semigroupk._
import io.circe.generic.auto._
import sttp.tapir.DecodeResult.Error
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerDefaults.StatusCodes
import sttp.tapir.server.http4s._
import sttp.tapir.server.{ DecodeFailureContext, ServerDefaults }
import sttp.tapir.server.{ DecodeFailureHandling }

import zio.interop.catz._
import zio.{ RIO, ZIO }
import org.http4s.dsl.Http4sDsl

class UserRoute[R <: UserRepository] extends Http4sDsl[RIO[R, *]] {
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

  private val getUserEndPoint = endpoint.get
    .in("user" / path[Long]("user id"))
    .errorOut(
      oneOf(
        statusMapping(StatusCodes.error, jsonBody[InternalServerErrorResponse]),
        statusMapping(StatusCodes.error, jsonBody[NotFoundResponse])
      )
    )
    .out(jsonBody[User])

  private val createUserEndPoint = endpoint.post
    .in("user")
    .in(jsonBody[User])
    .errorOut(
      oneOf[ErrorResponse](
        statusMapping(StatusCodes.error, jsonBody[InternalServerErrorResponse])
      )
    )
    .out(statusCode(StatusCodes.success))

  private val deleteUserEndPoint = endpoint.delete
    .in("user" / path[Long]("user id"))
    .errorOut(
      oneOf(
        statusMapping(StatusCodes.error, jsonBody[InternalServerErrorResponse]),
        statusMapping(StatusCodes.error, jsonBody[NotFoundResponse])
      )
    )
    .out(emptyOutput)

  val getRoutes: HttpRoutes[RIO[R, *]] = {
    getUserEndPoint.toRoutes(userId => handleError(getUser(userId))) <+> createUserEndPoint.toRoutes(user =>
      handleError(UserRepository.create(user))
    ) /* <+> deleteUserEndPoint.toRoutes { id =>
      val result = for {
        _    <- debug(s"id: $id")
        user <- getUser(id)
        _    <- delete(user.id)
      } yield {}

      handleError(result)
    } */
  }

  val getEndPoints = {
    List(getUserEndPoint, createUserEndPoint, deleteUserEndPoint)
  }

  private def getUser(userId: Long): ZIO[R, ExpectedFailure, User] =
    for {
      // _    <- debug(s"id: $userId")
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
