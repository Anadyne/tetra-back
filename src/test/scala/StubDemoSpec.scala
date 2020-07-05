package org.fsf.tetra

import org.fsf.tetra.model.database.User
import org.fsf.tetra.model.response.ErrorResponse
import org.fsf.tetra.route.Endpoints._

import sttp.client.Identity
import sttp.client._
import sttp.client.monad.MonadError
import sttp.client.monad.{ IdMonad }
import sttp.client.testing.SttpBackendStub
import sttp.tapir.client.sttp._

import zio.test.Assertion._
import zio.test._
import sttp.model._

object StubDemoSpec extends DefaultRunnableSpec {
  def spec = suite("Endpoints Spec")(
    test("Validate getUserEndpoint") {

      implicit val backend = SttpBackendStub[Identity, Nothing, NothingT](idMonad)
      // .withFallback(???)
        .whenRequestMatches(_.uri.path.startsWith(List("a", "b")))
        .thenRespond("Hello there!")
        .whenRequestMatches(_.method == Method.GET)
        .thenRespond(user)

      val resp  = getUserEndpoint.toSttpRequestUnsafe(uri"http://test.com").apply(11).send()
      val resp1 = basicRequest.get(uri"http://example.org/a/b/c").send()
      println(resp1)

      val exp: Response[Either[ErrorResponse, User]] = Response.ok(Right(user))

      assert(resp)(equalTo(exp))

    }
  )

  implicit val idMonad: MonadError[Identity] = IdMonad

  val user = User(0, "Boris", 10)

}
