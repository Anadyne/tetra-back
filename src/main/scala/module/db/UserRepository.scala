package org.fsf.tetra.module.db

import org.fsf.tetra.model.ExpectedFailure
import org.fsf.tetra.model.database.User
import zio.ZIO

trait UserRepository {
  val repository: UserRepository.Service
}

object UserRepository {

  trait Service {

    def get(id: Long): ZIO[Any, ExpectedFailure, Option[User]]

    def create(user: User): ZIO[Any, ExpectedFailure, Unit]

    def delete(id: Long): ZIO[Any, ExpectedFailure, Unit]
  }
}
