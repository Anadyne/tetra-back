package org.fsf.tetra.module.db

import zio.{ Has, Ref, ZIO, ZLayer }
import org.fsf.tetra.model.database.User
import org.fsf.tetra.model.{ DBFailure, ExpectedFailure }

import com.typesafe.config.Config
import io.getquill.{ H2JdbcContext, SnakeCase }

object userRepository {

  type UserRepository = Has[UserRepository.Service]
  type MockR          = Ref[Map[Long, User]]
  type MockHas        = Has[Ref[Map[Long, User]]]

  object UserRepository {

    trait Service {
      def get(id: Long): ZIO[Any, ExpectedFailure, Option[User]]
      def create(user: User): ZIO[Any, ExpectedFailure, Unit]
      def delete(id: Long): ZIO[Any, ExpectedFailure, Unit]
    }

    val any: ZLayer[UserRepository, Nothing, UserRepository] = ZLayer.requires[UserRepository]

    val live: ZLayer[UserRepository, Nothing, UserRepository] = ZLayer.succeed {
      new Service {
        val config: Config = ???

        lazy val ctx: H2JdbcContext[SnakeCase.type] = new H2JdbcContext(SnakeCase, config)
        import ctx._

        def get(id: Long): ZIO[Any, ExpectedFailure, Option[User]] =
          for {
            list <- ZIO.effect(ctx.run(query[User].filter(_.id == lift(id)))).mapError(t => DBFailure(t))
            user <- list match {
                     case Nil    => ZIO.none
                     case s :: _ => ZIO.some(s)
                   }
          } yield {
            user
          }

        def create(user: User): ZIO[Any, ExpectedFailure, Unit] =
          zio.IO
            .effect(ctx.run(query[User].insert(lift(user))))
            .mapError(t => DBFailure(t))
            .unit

        def delete(id: Long): ZIO[Any, ExpectedFailure, Unit] =
          zio.IO
            .effect(ctx.run(query[User].filter(_.id == lift(id)).delete))
            .mapError(t => DBFailure(t))
            .unit
      }
    }

    def get(id: Long): ZIO[UserRepository, ExpectedFailure, Option[User]] =
      ZIO.accessM(_.get.get(id))

    def create(user: User): ZIO[UserRepository, ExpectedFailure, Unit] =
      ZIO.accessM(_.get.create(user))

    def delete(id: Long): ZIO[UserRepository, ExpectedFailure, Unit] =
      ZIO.accessM(_.get.delete(id))

  }
}
