package org.fsf.tetra.module.logger

import org.fsf.tetra.implicits.Throwable._
import zio._
import zio.console.Console

// trait ConsoleLogger extends Logger.Service with Console.Service {

//   def error(message: => String): ZIO[Any, Nothing, Unit] = console.putStr(message)

//   def warn(message: => String): ZIO[Any, Nothing, Unit] = console.putStr(message)

//   def info(message: => String): ZIO[Any, Nothing, Unit] = console.putStr(message)

//   def debug(message: => String): ZIO[Any, Nothing, Unit] = console.putStr(message)

//   def trace(message: => String): ZIO[Any, Nothing, Unit] = console.putStr(message)

//   def error(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit] =
//     console.putStr(s"message: $message, exception: ${t.getStacktrace}")

//   def warn(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit] =
//     console.putStr(s"message: $message, exception: ${t.getStacktrace}")

//   def info(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit] =
//     console.putStr(s"message: $message, exception: ${t.getStacktrace}")

//   def debug(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit] =
//     console.putStr(s"message: $message, exception: ${t.getStacktrace}")

//   def trace(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit] =
//     console.putStr(s"message: $message, exception: ${t.getStacktrace}")
// }

object logger {
  type Logger      = Has[Logger.Service]
  type StringThunk = () => String

  object Logger {
    trait Service {
      def error(message: => String): ZLayer[Any, Nothing, Has[Unit]]
      def warn(message: => String): ZLayer[Any, Nothing, Has[Unit]]
      def info(message: => String): ZLayer[Any, Nothing, Has[Unit]]
      def debug(message: => String): ZLayer[Any, Nothing, Has[Unit]]
      def trace(message: => String): ZLayer[Any, Nothing, Has[Unit]]
      def errorT(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit]
      // def warn(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit]
      // def info(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit]
      // def debug(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit]
      // def trace(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit]
    }
    val any: ZLayer[Logger, Nothing, Logger] = ZLayer.requires[Logger]

    val live: ZLayer[Console, Nothing, Has[Unit]] = ZLayer.fromFunction { console: Console =>
      def error(message: => String): ZIO[Any, Nothing, Unit] = console.get.putStr(message)

      def warn(message: => String): ZIO[Any, Nothing, Unit] = console.get.putStr(message)

      def info(message: => String): ZIO[Any, Nothing, Unit] = console.get.putStr(message)

      def debug(message: => String): ZIO[Any, Nothing, Unit] = console.get.putStr(message)

      def trace(message: => String): ZIO[Any, Nothing, Unit] = console.get.putStr(message)

      def errorT(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit] =
        console.get.putStr(s"message: $message, exception: ${t.getStacktrace}")

    // def warn(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit] =
    //   console.putStr(s"message: $message, exception: ${t.getStacktrace}")

    // def info(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit] =
    //   console.putStr(s"message: $message, exception: ${t.getStacktrace}")

    // def debug(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit] =
    //   console.putStr(s"message: $message, exception: ${t.getStacktrace}")

    // def trace(t: Throwable)(message: => String): ZIO[Any, Nothing, Unit] =
    //   console.putStr(s"message: $message, exception: ${t.getStacktrace}")

    }
  }
}
