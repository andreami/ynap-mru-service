package com.ynap.mru

import cats.Applicative
import cats.effect.{IO, Sync}
import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.{EntityDecoder, EntityEncoder, Method, Request, _}
import org.scalatest.FunSuite

class PingPongTests extends FunSuite {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  case class Pong(value: String)
  object Pong {
    implicit val pongEncoder: Encoder[Pong] = deriveEncoder[Pong]
    implicit val pongDecoder: Decoder[Pong] = deriveDecoder[Pong]
    implicit def pongEntityDecoder[F[_] : Sync]: EntityDecoder[F, Pong] = jsonOf
    implicit def pongEntityEncoder[F[_] : Applicative]: EntityEncoder[F, Pong] = jsonEncoderOf
  }

  case class PongRes(value: String)
  object PongRes {
    implicit val pongResEncoder: Encoder[PongRes] = deriveEncoder[PongRes]
    implicit val pongResDecoder: Decoder[PongRes] = deriveDecoder[PongRes]
    implicit def pongResEntityDecoder[F[_] : Sync]: EntityDecoder[F, PongRes] = jsonOf
    implicit def pongResEntityEncoder[F[_] : Applicative]: EntityEncoder[F, PongRes] = jsonEncoderOf
  }

  test("it works") {
    val req = Request[IO](Method.GET, uri"/ping")

    val route: HttpRoutes[IO] = HttpRoutes.of[IO] {
      case GET -> Root / "ping" =>
        for {
          str <- IO.pure("pong")
          resp <- Ok(Pong(str))
        } yield resp
    }

    val app: HttpApp[IO] = route.orNotFound
    val response: Response[IO] = runApp(app, req)
    val body = response.as[String].unsafeRunSync()

    assert(response.status == Status.Ok)
    assert(body == expected(PongRes("pong")))
  }

  def runApp(app: HttpApp[IO], req: Request[IO]): Response[IO] =
    app.run(req).unsafeRunSync()

  def expected[A](a: A)(implicit E: Encoder[A]): String = a.asJson.noSpaces
}
