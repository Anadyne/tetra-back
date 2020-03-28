package org.fsf.tetra.module.logger

import zio.{ Has, ZIO, ZLayer }
import zio.console.Console

object logger {
  type Logger = Has[Logger.Service]

  object Logger {
    trait Service {
      def error(message: => String): ZIO[Any, Nothing, Unit]
      def warn(message: => String): ZIO[Any, Nothing, Unit]
      def info(message: => String): ZIO[Any, Nothing, Unit]
      def debug(message: => String): ZIO[Any, Nothing, Unit]
      def trace(message: => String): ZIO[Any, Nothing, Unit]
    }
    val any: ZLayer[Logger, Nothing, Logger] = ZLayer.requires[Logger]

    val live = ZLayer.fromFunction { console: Console =>
      new Service {

        def error(message: => String): ZIO[Any, Nothing, Unit] = console.get.putStr(message)
        def warn(message: => String): ZIO[Any, Nothing, Unit]  = console.get.putStr(message)
        def info(message: => String): ZIO[Any, Nothing, Unit]  = console.get.putStr(message)
        def debug(message: => String): ZIO[Any, Nothing, Unit] = console.get.putStr(message)
        def trace(message: => String): ZIO[Any, Nothing, Unit] = console.get.putStr(message)

      }
    }
  }
}
