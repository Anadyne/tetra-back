package org.fsf.tetra.module.logger

import cats.Show
import zio.{ Has, ZIO }

object logger {
  type Logger = Has[Logger.Service[Any]]

  object Logger {
    trait Service[R] extends Serializable {
      def trace[A: Show](
        a: => A
      )(
        implicit
        line: sourcecode.Line,
        file: sourcecode.File
      ): ZIO[R, Nothing, Unit]

      def debug[A: Show](
        a: => A
      )(
        implicit
        line: sourcecode.Line,
        file: sourcecode.File
      ): ZIO[R, Nothing, Unit]

      def info[A: Show](
        a: => A
      )(
        implicit
        line: sourcecode.Line,
        file: sourcecode.File
      ): ZIO[R, Nothing, Unit]

      def warn[A: Show](
        a: => A
      )(
        implicit
        line: sourcecode.Line,
        file: sourcecode.File
      ): ZIO[R, Nothing, Unit]

      def error[A: Show](
        a: => A
      )(
        implicit
        line: sourcecode.Line,
        file: sourcecode.File
      ): ZIO[R, Nothing, Unit]

    }
  }

  trait UnsafeLogger {
    def withContext(ctx: String): UnsafeLogger

    def trace[A: Show](
      a: => A
    )(
      implicit
      line: sourcecode.Line,
      file: sourcecode.File
    ): Unit

    def debug[A: Show](
      a: => A
    )(
      implicit
      line: sourcecode.Line,
      file: sourcecode.File
    ): Unit

    def info[A: Show](
      a: => A
    )(
      implicit
      line: sourcecode.Line,
      file: sourcecode.File
    ): Unit

    def warn[A: Show](
      a: => A
    )(
      implicit
      line: sourcecode.Line,
      file: sourcecode.File
    ): Unit

    def error[A: Show](
      a: => A
    )(
      implicit
      line: sourcecode.Line,
      file: sourcecode.File
    ): Unit
  }

}
