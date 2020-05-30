package org.fsf.tetra.client

import sttp.client.RequestT
import sttp.client._

import zio.{ ZEnv }

object Common {
  private[client] val client = new Client()

  private[client] def runme[A](req: RequestT[sttp.client.Identity, A, Nothing]) =
    client.run[A](req).provideLayer(ZEnv.live)

  // Basic REST API
  val baseAuthReq: RequestT[Empty, String, Nothing] =
    basicRequest.response(asStringAlways).auth.bearer("f2a86c90-2ed4-11ea-a06d-359c481f3a21")

  val okEmpty: Response[Unit] = Response.ok(())

}
