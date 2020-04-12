package org.fsf.tetra

import org.fsf.tetra.module.db.LiveServices.UserRepository
import org.fsf.tetra.module.logger.logger.Logger

import zio.clock.Clock
import zio.{ RIO, Ref }
import org.fsf.tetra.model.database.User

object types {
  type MockType       = Ref[Map[Long, User]]
  type AppEnvironment = Clock with UserRepository with Logger
  type AppTask[+A]    = RIO[AppEnvironment, A]
}
