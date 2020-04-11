package org.fsf.tetra.client

import io.circe.generic.auto._

import org.fsf.tetra.model.database.User
import org.fsf.tetra.types._

import com.typesafe.scalalogging.LazyLogging

import sttp.client._
import sttp.client.asynchttpclient.zio._
import sttp.client.circe._
import sttp.model.Uri

import zio.console.{ putStrLn, Console }
import zio.{ URIO, ZEnv, ZIO }

class Client() extends LazyLogging {

  def run(link: Uri, tpe: ReqType): URIO[ZEnv, Int] = {
    logger.debug(">>>>>> 1")

    val request = tpe match {
      case POST =>
        basicRequest
          .post(link)
          .response(asJson[User])
      case GET =>
        basicRequest
          .get(link)
          // .response(asStringAlways)
          .response(asJson[User])
    }

    logger.debug(">>>>>> 2")

    // create a description of a program, which requires two dependencies in the environment:
    // the SttpClient, and the Console
    val sendAndPrint: ZIO[Console with SttpClient, Throwable, Unit] = {
      logger.debug(">>>>>>>>>>>  Sending sttp request ")
      for {
        response <- SttpClient.send(request)
        _        <- putStrLn(s"Got response code: ${response.code}")
        _        <- putStrLn(response.body.toString)
      } yield ()
    }

    logger.debug(">>>>>> 3")

    // provide an implementation for the SttpClient dependency; other dependencies are
    // provided by Zio
    sendAndPrint.provideCustomLayer(AsyncHttpClientZioBackend.layer()).fold(_ => 1, _ => 0)
  }

}
