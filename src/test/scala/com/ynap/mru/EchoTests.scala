package com.ynap.mru

import cats._
import cats.effect._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._
import org.http4s.Method._
import org.http4s._
import org.http4s.circe._
import org.http4s.client.dsl.io._
import org.scalatest.FunSuite

class EchoTests extends FunSuite {

  case class EchoReq(value: String)

  object EchoReq {
    implicit val pongReqEncoder: Encoder[EchoReq] = deriveEncoder[EchoReq]
    implicit val pongReqDecoder: Decoder[EchoReq] = deriveDecoder[EchoReq]

    implicit def pongReqEntityDecoder[F[_] : Sync]: EntityDecoder[F, EchoReq] = jsonOf

    implicit def pongReqEntityEncoder[F[_] : Applicative]: EntityEncoder[F, EchoReq] = jsonEncoderOf
  }

  case class EchoRes(value: String)

  object EchoRes {
    implicit val pongResEncoder: Encoder[EchoRes] = deriveEncoder[EchoRes]
    implicit val pongResDecoder: Decoder[EchoRes] = deriveDecoder[EchoRes]

    implicit def pongResEntityDecoder[F[_] : Sync]: EntityDecoder[F, EchoRes] = jsonOf

    implicit def pongResEntityEncoder[F[_] : Applicative]: EntityEncoder[F, EchoRes] = jsonEncoderOf
  }

  test("it works") {
    val scenario = for {
      req <- POST(EchoReq("ciao").asJson, uri"/echo")
      res <- MruServer.app.run(req)
      body <- res.as[EchoRes]
    } yield (res.status, body)

    val (status, body) = scenario.unsafeRunSync()

    assert(status == Status.Ok)
    assert(body == EchoRes("ciao ciao"))
  }
}
