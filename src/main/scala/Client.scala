package org.fsf.tetra.client

import io.circe.generic.auto._

import org.fsf.tetra.model.database.User

import sttp.client._
import sttp.client.asynchttpclient.zio._
import sttp.client.circe._
import sttp.model.Uri

import zio.console.{ putStrLn, Console }
import zio.{ ZIO }

class Client() {

  def run(link: Uri) = {
    val request = basicRequest
      .get(link)
      .response(asJson[User])

    // create a description of a program, which requires two dependencies in the environment:
    // the SttpClient, and the Console
    val sendAndPrint: ZIO[Console with SttpClient, Throwable, Unit] = for {
      response <- SttpClient.send(request)
      _        <- putStrLn(s"Got response code: ${response.code}")
      _        <- putStrLn(response.body.toString)
    } yield ()

    // provide an implementation for the SttpClient dependency; other dependencies are
    // provided by Zio
    sendAndPrint.provideCustomLayer(AsyncHttpClientZioBackend.layer()).fold(_ => 1, _ => 0)
  }

}
