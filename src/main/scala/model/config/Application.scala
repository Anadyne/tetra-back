package org.fsf.tetra.model.config

import java.net.InetAddress

import ciris.api.Id
import ciris.refined._
import ciris.{ env, loadConfig, ConfigResult }
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.types.net.UserPortNumber

final case class Application(
  server: Server,
  database: Database
)

object Application {

  private val config = load(
    env("server.host"),
    env("server.port"),
    env("dataSource.className"),
    env("dataSource.url"),
    env("dataSource.user"),
    env("dataSource.password")
  ) { (url, port, className, dataSourceUrl, user, password) =>
    Application(Server(url, port), Database(className, dataSourceUrl, user, password))
  }

  val getConfig: Application = config.orThrow()
}
