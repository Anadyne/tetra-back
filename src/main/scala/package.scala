package org.fsf.tetra

import org.fsf.tetra.module.db.userRepository.UserRepository
import org.fsf.tetra.module.logger.logger.Logger

import zio.{ RIO, ZEnv }

object types {
  type AppEnvironment = ZEnv with UserRepository with Logger
  type AppTask[A]     = RIO[AppEnvironment, A]
}
