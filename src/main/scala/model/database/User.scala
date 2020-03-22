package org.fsf.tetra.model.database
import io.circe.generic.semiauto._
import io.circe.{ Decoder, Encoder }

final case class User(id: Long, name: String, age: Int)
object User {
  implicit val fooDecoder: Decoder[User] = deriveDecoder[User]
  implicit val fooEncoder: Encoder[User] = deriveEncoder[User]
}
