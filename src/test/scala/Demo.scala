package org.fsf.tetra

import java.net.URI

import io.circe.generic.auto._

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

object DemoSpec extends DefaultRunnableSpec {
  def spec = suite("Tapir Demo")(
    test("Validate Simplest Endpoint") {
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
}
