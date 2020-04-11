package org.fsf.tetra.client

import org.fsf.tetra.client.Client
import org.fsf.tetra.model.database.User

import sttp.client._

import zio.test.Assertion._
import zio.test._
import zio.{ ZEnv }

object RoutesSpec extends DefaultRunnableSpec {
  def spec = suite("Routes Spec")(
    testM("Hello World Request") {

      val req: RequestT[sttp.client.Identity, String, Nothing] = basicRequest
        .get(uri"http://localhost:5566/hello?name=Boris")
        .response(asStringAlways)

      val res = client.run[String](req).provideLayer(ZEnv.live)

      assertM(res)(equalTo(0))
    }
  )

  val client = new Client()
  val user   = User(0, "Boris", 34)
}
