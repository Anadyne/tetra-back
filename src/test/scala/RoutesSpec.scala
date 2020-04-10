package org.fsf.tetra.client

import io.circe.generic.auto._

import sttp.client._
import sttp.client.asynchttpclient.zio._
import sttp.client.circe._

import zio.console.{ putStrLn, Console }
import zio.test.Assertion._
import zio.test._
import zio.{ ZIO }
import org.fsf.tetra.model.database.User

object RoutesSpec extends DefaultRunnableSpec {
  def spec = suite("Routes Spec")(
    testM("Validate getUserEndpoint") {

      case class HttpBinResponse(origin: String, headers: Map[String, String])

      val request = basicRequest
        .get(uri"https://httpbin.org/post")
        // .post(uri"http://localhost:5566/user?")
        // .get(uri"http://localhost:5566/docs")
        .response(asJson[HttpBinResponse])

      // create a description of a program, which requires two dependencies in the environment:
      // the SttpClient, and the Console
      val sendAndPrint: ZIO[Console with SttpClient, Throwable, Unit] = for {
        response <- SttpClient.send(request)
        _        <- putStrLn(s"Got response code: ${response.code}")
        _        <- putStrLn(response.body.toString)
      } yield ()

      // provide an implementation for the SttpClient dependency; other dependencies are
      // provided by Zio
      // sendAndPrint.provideCustomLayer(AsyncHttpClientZioBackend.layer()).fold(_ => 1, _ => 0)
      val out = sendAndPrint.provideCustomLayer(AsyncHttpClientZioBackend.layer())

      assertM(out)(isUnit)
    }
  )

  val user = User(0, "Boris", 10)
}
