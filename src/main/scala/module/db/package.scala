package org.fsf.tetra.module

import org.fsf.tetra.model.ExpectedFailure
import org.fsf.tetra.model.database.User
import zio.ZIO

package object db {

  def get(id: Long): ZIO[UserRepository, ExpectedFailure, Option[User]] =
    ZIO.accessM(_.repository.get(id))

  def create(user: User): ZIO[UserRepository, ExpectedFailure, Unit] =
    ZIO.accessM(_.repository.create(user))

  def delete(id: Long): ZIO[UserRepository, ExpectedFailure, Unit] =
    ZIO.accessM(_.repository.delete(id))
}
