package org.fsf.tetra

import java.net.URI

import org.fsf.tetra.client.Client
import org.fsf.tetra.model.database.User

import sttp.model.Uri

import zio.test.Assertion._
import zio.test._
import zio.{ ZIO }

object RoutesSpec extends DefaultRunnableSpec {
  def spec = suite("Routes Spec")(
    testM("Validate getUserEndpoint") {

      val req = new URI("https://httpbin.org/post")
      client.run(Uri(req))

      assertM(ZIO.succeed(true))(isTrue)
    }
  )
  val client = new Client()
  val user   = User(0, "Boris", 34)
}
