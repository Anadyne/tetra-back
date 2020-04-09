package org.fsf.tetra.route

import io.circe.generic.auto._

import org.http4s.HttpRoutes
import org.http4s._

import sttp.tapir.Endpoint
import sttp.tapir._
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerOptions
import sttp.tapir.server.http4s._
import sttp.tapir.server.http4s._

import zio.interop.catz._
import zio.{ IO, Task, UIO }

object MyRoute {
  implicit class ZioEndpoint[I, E, O](e: Endpoint[I, E, O, EntityBody[Task]]) {
    def toZioRoutes(logic: I => IO[E, O])(implicit serverOptions: Http4sServerOptions[Task]): HttpRoutes[Task] = {
      import sttp.tapir.server.http4s._
      e.toRoutes(i => logic(i).either)
    }

    def zioServerLogic(logic: I => IO[E, O]): ServerEndpoint[I, E, O, EntityBody[Task], Task] =
      ServerEndpoint(e, logic(_).either)
  }

  final case class Pet(name: String)

  // Sample endpoint, with the logic implemented directly using .toZioRoutes
  val petEndpoint: Endpoint[Int, String, Pet, Nothing] =
    endpoint.get.in("pet" / path[Int]("petId")).errorOut(stringBody).out(jsonBody[Pet])

  val petRoutes: HttpRoutes[Task] = petEndpoint.toZioRoutes(_ => UIO(Pet("Tapirus")))
}
