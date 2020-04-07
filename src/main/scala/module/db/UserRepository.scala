package org.fsf.tetra.module.db

import scala.jdk.CollectionConverters._

import io.getquill.{ H2JdbcContext, SnakeCase }

import org.fsf.tetra.model.database.User
import org.fsf.tetra.model.{ DBFailure, ExpectedFailure }

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import zio.{ Has, ZIO, ZLayer }

object ExtServices {

  type UserRepository = Has[UserRepository.Service]

  object UserRepository {

    trait Service {
      def get(id: Long): ZIO[Any, ExpectedFailure, Option[User]]
      def create(user: User): ZIO[Any, ExpectedFailure, Unit]
      def delete(id: Long): ZIO[Any, ExpectedFailure, Unit]
    }

    val live = ZLayer.fromService { cfg: Config =>
      new Service {

        private lazy val ctx: H2JdbcContext[SnakeCase.type] = new H2JdbcContext(SnakeCase, cfg)
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

  val config = {
    val map = Map(
      "dataSourceClassName" -> "",
      "dataSource.url"      -> "",
      "dataSource.user"     -> "",
      "dataSource.password" -> ""
    ).asJava

    ConfigFactory.parseMap(map)
  }

  val liveEnv = ZLayer.succeed(config) >>> UserRepository.live
}
