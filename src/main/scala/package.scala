package org.fsf.tetra

import org.fsf.tetra.model.database.User
import org.fsf.tetra.module.db.UserRepository
import org.fsf.tetra.module.logging.AppLogger.Logger

import zio.clock.Clock
import zio.{ Has, RIO, Ref }

object types {

  type AppEnvironment = Clock with UserRepository with Logger
  type AppTask[+A]    = RIO[AppEnvironment, A]

  type UserRepository = Has[UserRepository.Service]

  type MockType = Ref[Map[Long, User]]

}
