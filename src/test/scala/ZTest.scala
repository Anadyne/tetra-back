package org.fsf.tetra

import java.net.URI

import io.circe.generic.auto._

import org.fsf.tetra.model.database.User
import org.fsf.tetra.module.db.ExtServices.UserRepository
import org.fsf.tetra.module.logger.logger.Logger
import org.fsf.tetra.route.UserRoute

import sttp.client.Identity
import sttp.client._
import sttp.client.monad.MonadError
import sttp.client.monad._
import sttp.client.testing.SttpBackendStub
import sttp.model.Uri
import sttp.tapir._
import sttp.tapir.client.sttp._
import sttp.tapir.json.circe._
import sttp.tapir.server.stub._

import zio.test.Assertion._
import zio.test._
import zio.{ Ref, ZEnv }

object ZSpec extends DefaultRunnableSpec {
  def spec = suite("ZSpec")(
    test("blah") {
      val endpoint = sttp.tapir.endpoint
        .in("api" / "sometest4")
        .in(query[Int]("amount"))
        .post
        .out(jsonBody[ResponseWrapper])

      implicit val backend = SttpBackendStub
        .apply(idMonad)
        .whenRequestMatches(endpoint)
        .thenSuccess(ResponseWrapper(1.0))

      val resp = endpoint.toSttpRequestUnsafe(Uri(new URI("http://test.com"))).apply(11).send()
      println(resp)

      val exp: Response[Either[Unit, ResponseWrapper]] = Response.ok(Right(ResponseWrapper(1.0)))

      assert(resp)(equalTo(exp))

    }
  )

  final case class ResponseWrapper(response: Double)
  implicit val idMonad: MonadError[Identity] = IdMonad

  val repo = Ref.make(Map.empty[Long, User])

  val user = User(0, "Boris", 10)
  val app  = new UserRoute().getRoutes
  val env  = ZEnv.live ++ UserRepository.live ++ Logger.live

}
