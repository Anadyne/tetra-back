package org.fsf.tetra
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import org.fsf.tetra.model.database.User
import org.fsf.tetra.module.db.mockRepository
import org.fsf.tetra.module.db.userRepository.UserRepository
import org.fsf.tetra.module.logger.logger.Logger
import org.fsf.tetra.route.UserRoute
import org.http4s.EntityEncoder
import org.http4s.Method
import org.http4s.Request
import org.http4s.implicits._
import org.http4s.circe._

import HTTPSpec._

import zio._
import zio.test.Assertion._
import zio.test._

object helper {
  type Env = UserRepository with Logger
  private val userRoute: UserRoute[Env] = new UserRoute[Env]

  userRoute.getRoutes

  val env = ZEnv.live ++ UserRepository.live ++ Logger.live

}

object ZSpec extends DefaultRunnableSpec {
  def spec = suite("ZSpec")(
    testM("blah") {
      type UTask = zio.Task[User]
      // implicit val e: EntityEncoder[zio.Task, User] =
      //   jsonEncoderOf[zio.Task, User]
      // implicit val e: EntityEncoder[IO, User] =
      //   jsonEncoderOf[IO, User]
      // val payload = request(Method.POST, "/user").withEntity(user)
      val req = Request(Method.GET, uri"""/blah/blah""")

      assertM(ZIO.succeed(true))(isTrue)
    }
  )

  val repo = Ref.make(Map.empty[Long, User])

  val user = User(0, "Boris", 10)

}
