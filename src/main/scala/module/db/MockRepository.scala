package org.fsf.tetra.module.db

import io.circe.generic.auto._, io.circe.syntax._

import org.fsf.tetra.model.database.User
import org.fsf.tetra.model.{ ExpectedFailure }
import org.fsf.tetra.types.MockType

import zio.{ Has, ZIO, ZLayer }

object MockServices {
  type UserRepository = Has[UserRepository.Service]

  object UserRepository {

    trait Service {
      def hello(name: String): ZIO[Any, ExpectedFailure, String]
      def get(id: Long): ZIO[Any, ExpectedFailure, Option[User]]
      def create(user: User): ZIO[Any, ExpectedFailure, Unit]
      def delete(id: Long): ZIO[Any, ExpectedFailure, Unit]
    }

    val live: ZLayer[Has[MockType], Nothing, Has[Service]] = ZLayer.fromService { ref: MockType =>
      new Service {

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
}
