package org.fsf.tetra.model.config

import pureconfig.ConfigSource
import pureconfig.generic.auto._

object config {

  final case class Server(host: String, port: Int)

  final case class Database(
    source: String,
    url: String,
    user: String,
    pass: String
  )

  final case class AppConfig(
    server: Server,
    database: Database
  )

  def loadConfig = ConfigSource.default.load[AppConfig]
}
