package org.fsf.tetra.client

import com.typesafe.scalalogging.LazyLogging

import sttp.client.RequestT
import sttp.client.asynchttpclient.zio._

import zio.console.{ putStrLn, Console }
import zio.{ URIO, ZEnv, ZIO }

class Client() extends LazyLogging {

  def run[A](req: RequestT[sttp.client.Identity, A, Nothing]): URIO[ZEnv, Int] = {

    // create a description of a program, which requires two dependencies in the environment:
    // the SttpClient, and the Console
    val sendAndPrint: ZIO[Console with SttpClient, Throwable, Unit] = {
      logger.debug(">>>>>>>>>>>  Sending sttp request ")
      for {
        response <- SttpClient.send(req)
        _        <- putStrLn(s"Got response code: ${response.code}")
        _        <- putStrLn(response.body.toString)
      } yield ()
    }

    // provide an implementation for the SttpClient dependency; other dependencies are
    // provided by Zio
    sendAndPrint.provideCustomLayer(AsyncHttpClientZioBackend.layer()).fold(_ => 1, _ => 0)
  }

}
