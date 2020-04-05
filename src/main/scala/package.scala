package org.fsf.tetra

import org.fsf.tetra.module.db.ExtServices.UserRepository
import org.fsf.tetra.module.logger.logger.Logger

import com.typesafe.config.Config

import zio.clock.Clock
import zio.{ RIO, ZEnv }

object types {
  type AppEnvironment = ZEnv with UserRepository with Logger
  type AppTask[A]     = RIO[AppEnvironment, A]
}
