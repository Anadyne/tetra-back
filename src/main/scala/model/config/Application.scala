package org.fsf.tetra.model.config

import java.net.InetAddress

import scala.jdk.CollectionConverters._
import com.typesafe.config.{ ConfigFactory }

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

  private val config: ConfigResult[Id, Application] = loadConfig(
    env[InetAddress]("server.host"),
    env[UserPortNumber]("server.port"),
    env[Refined[String, NonEmpty]]("dataSource.className"),
    env[Refined[String, NonEmpty]]("dataSource.url"),
    env[Refined[String, NonEmpty]]("dataSource.user"),
    env[Refined[String, NonEmpty]]("dataSource.password")
  ) { (url, port, className, dataSourceUrl, user, password) =>
    Application(Server(url, port), Database(className, dataSourceUrl, user, password))
  }

  val getConfig: Application = config.orThrow()

  def appConfig(cfg: Application) = ConfigFactory.parseMap {
    Map(
      "dataSourceClassName" -> cfg.database.className.value,
      "dataSource.url"      -> cfg.database.url.value,
      "dataSource.user"     -> cfg.database.user.value,
      "dataSource.password" -> cfg.database.password.value
    ).asJava
  }
}
