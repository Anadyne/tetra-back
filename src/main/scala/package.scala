package org.fsf.tetra

import org.fsf.tetra.module.db.ExtServices.UserRepository
import org.fsf.tetra.module.logger.logger.Logger

import zio.{ RIO }
import zio.clock.Clock

object types {
  type AppEnvironment = Clock with UserRepository with Logger
  type AppTask[A]     = RIO[AppEnvironment, A]
}
