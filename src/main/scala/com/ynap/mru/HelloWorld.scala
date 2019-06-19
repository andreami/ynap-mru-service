package com.ynap.mru

import cats.Applicative
import cats.implicits._
import cats.effect._
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait HelloWorld{
  def hello(n: HelloWorld.Name): IO[HelloWorld.Greeting]
}

object HelloWorld {
  implicit def apply[F[_]](implicit ev: HelloWorld): HelloWorld = ev

  final case class Name(name: String) extends AnyVal
  /**
    * More generally you will want to decouple your edge representations from
    * your internal data structures, however this shows how you can
    * create encoders for your data.
    **/
  final case class Greeting(greeting: String) extends AnyVal
  object Greeting {
    implicit val greetingEncoder: Encoder[Greeting] = new Encoder[Greeting] {
      final def apply(a: Greeting): Json = Json.obj(
        ("message", Json.fromString(a.greeting)),
      )
    }
    implicit def greetingEntityEncoder: EntityEncoder[IO, Greeting] =
      jsonEncoderOf[IO, Greeting]
  }

  def impl: HelloWorld = new HelloWorld {
    def hello(n: HelloWorld.Name): IO[HelloWorld.Greeting] =
        IO.pure(Greeting("Hello, " + n.name))
  }
}