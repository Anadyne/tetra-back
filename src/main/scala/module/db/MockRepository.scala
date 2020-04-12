package org.fsf.tetra.module.db

import io.circe.generic.auto._, io.circe.syntax._

import org.fsf.tetra.model.database.User
import org.fsf.tetra.model.{ ExpectedFailure }
import org.fsf.tetra.module.db.ExtServices._

import zio.{ Has, Ref, ZIO, ZLayer }

object MockRepository {
  type MockType = Ref[Map[Long, User]]

  val live: ZLayer[MockType, Nothing, UserRepository] = ZLayer.fromFunction { ref: MockType =>
    new UserRepository.Service {

      def hello(name: String): ZIO[Any, ExpectedFailure, String] = ZIO.succeed(User(13, "Boris", 34).asJson.toString)
      def get(id: Long): ZIO[Any, ExpectedFailure, Option[User]] =
        for {
          user <- ref.get.map(_.get(id))
          out <- user match {
                  case Some(s) => ZIO.some(s)
                  case None    => ZIO.none
                }
        } yield out

      def create(user: User): ZIO[Any, ExpectedFailure, Unit] = ref.update(map => map.+(user.id -> user)).unit

      def delete(id: Long): ZIO[Any, ExpectedFailure, Unit] = ref.update(map => map.-(id)).unit
    }
  }
}
