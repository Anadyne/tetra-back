package org.fsf.tetra.client

import sttp.client._

import zio.test.Assertion._
import zio.test.TestAspect.ignore
import zio.test._
import Common._

object AuthSpec extends DefaultRunnableSpec {
  def spec = suite("Auth Spec")(
    testM("Media Block List") {

      val req =
        baseAuthReq.post(uri"http://localhost:8443/media/uploads/images/d44950c4-831c-11ea-94c7-0f14f54469a8/list")

      assertM(runme(req))(equalTo(okEmpty))
    } @@ ignore,
    testM("FinStats isPayer") {

      val req = baseAuthReq.post(uri"http://localhost:8443/users/stats/financial/online-donators-percent")

      assertM(runme(req))(equalTo(okEmpty))
    }
  )
}
