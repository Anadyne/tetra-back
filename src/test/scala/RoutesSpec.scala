package org.fsf.tetra.client

import java.net.URI

import org.fsf.tetra.client.Client
import org.fsf.tetra.model.database.User
import org.fsf.tetra.types._

import sttp.model.Uri

import zio.test.Assertion._
import zio.test._
import zio.{ ZEnv }

object RoutesSpec extends DefaultRunnableSpec {
  def spec = suite("Routes Spec")(
    testM("Validate getUserEndpoint") {

      val req = new URI("http://localhost:5566/user/0")
      // val req = new URI("http://localhost:5566/docs")
      val res = client.run(Uri(req), POST).provideLayer(ZEnv.live)

      assertM(res)(equalTo(0))
    }
  )
  val client = new Client()
  val user   = User(0, "Boris", 34)
}
