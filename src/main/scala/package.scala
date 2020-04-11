package org.fsf.tetra

import org.fsf.tetra.module.db.ExtServices.UserRepository
import org.fsf.tetra.module.logger.logger.Logger

import zio.clock.Clock
import zio.{ RIO }

object types {
  type AppEnvironment = Clock with UserRepository with Logger
  type AppTask[+A]    = RIO[AppEnvironment, A]

  sealed trait ReqType
  final case object POST extends ReqType
  final case object GET  extends ReqType

}
