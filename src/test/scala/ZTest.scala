package org.fsf.tetra

import org.fsf.tetra.model.database.User
import org.fsf.tetra.module.db.userRepository.UserRepository
import org.fsf.tetra.module.logger.logger.Logger
import org.fsf.tetra.route.UserRoute
import org.http4s.Method
import org.http4s.Request
import org.http4s.implicits._

import zio._
import zio.test.Assertion._
import zio.test._

object ZSpec extends DefaultRunnableSpec {
  def spec = suite("ZSpec")(
    testM("blah") {

      val req = Request(Method.GET, uri"""/user""")

      val rsp = app.run(???)

      assertM(ZIO.succeed(true))(isTrue)
    }
  )

  val repo = Ref.make(Map.empty[Long, User])

  val user = User(0, "Boris", 10)
  val app  = new UserRoute().getRoutes
  val env  = ZEnv.live ++ UserRepository.live ++ Logger.live

}
