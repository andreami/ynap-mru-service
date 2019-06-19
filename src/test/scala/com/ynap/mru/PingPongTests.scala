package com.ynap.mru

import cats.Applicative
import cats.effect.IO
import io.circe.literal._
import io.circe.{Encoder, Json}
import org.http4s.{EntityEncoder, _}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.scalatest.FunSuite
import io.circe.generic.auto._
import io.circe.syntax._

class PingPongTests extends FunSuite {

  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}

  import dsl._

  case class Pong(message: String)
  object Pong {
    implicit def greetingEntityEncoder[F[_] : Applicative](implicit E:Encoder[Pong]): EntityEncoder[F, Pong] =
      jsonEncoderOf[F, Pong]
  }

  case class PongRes(value: String)
  object PongRes {
    implicit def greetingEntityEncoder[F[_] : Applicative](implicit E:Encoder[PongRes]): EntityEncoder[F, PongRes] =
      jsonEncoderOf[F, PongRes]
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
