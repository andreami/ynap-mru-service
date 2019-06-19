package com.ynap.mru

import cats._
import cats.effect._

import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe._

import org.http4s._
import org.http4s.circe._
import org.http4s.client.dsl.io._
import org.http4s.Method._

import org.scalatest.FunSuite

class PingPongTests extends FunSuite {

  case class PongRes(value: String)

  object PongRes {
    implicit val pongResEncoder: Encoder[PongRes] = deriveEncoder[PongRes]
    implicit val pongResDecoder: Decoder[PongRes] = deriveDecoder[PongRes]

    implicit def pongResEntityDecoder[F[_] : Sync]: EntityDecoder[F, PongRes] = jsonOf
    implicit def pongResEntityEncoder[F[_] : Applicative]: EntityEncoder[F, PongRes] = jsonEncoderOf
  }

  test("it works") {

    val scenario = for {
      req <- GET(uri"/ping")
      res <- MruServer.app.run(req)
      body <- res.as[String]
    } yield (res.status, body)

    val (status, body) = scenario.unsafeRunSync()

    assert(status == Status.Ok)
    assert(body == expected(PongRes("ok")))
  }

  def expected[A](a: A)(implicit E: Encoder[A]): String =
    a.asJson.noSpaces
}
