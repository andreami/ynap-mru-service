package com.ynap.mru

import cats.Applicative
import cats.effect.{IO, Sync}
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}

object MruRoutes {

  def helloWorldRoutes(H: HelloWorld): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      for {
        greeting <- H.hello(HelloWorld.Name(name))
        resp <- Ok(greeting)
      } yield resp
  }

  case class Pong(value: String)

  object Pong {
    implicit def pongEntityDecoder[F[_] : Sync]: EntityDecoder[F, Pong] = jsonOf
    implicit def pongEntityEncoder[F[_] : Applicative]: EntityEncoder[F, Pong] = jsonEncoderOf
  }

  val ping: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "ping" =>
      Ok(Pong("ok"))
  }

  case class Echo(value: String)

  object Echo {
    implicit def pongEntityDecoder[F[_] : Sync]: EntityDecoder[F, Echo] = jsonOf
    implicit def pongEntityEncoder[F[_] : Applicative]: EntityEncoder[F, Echo] = jsonEncoderOf
  }

  val echo: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "echo" =>
      for {
        e <- req.as[Echo]
        res <- Ok(Echo(s"${e.value} ${e.value}"))
      } yield res
  }
}