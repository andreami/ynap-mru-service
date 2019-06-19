package com.ynap.mru

import cats.data.Kleisli
import cats.effect._
import fs2.Stream
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

object MruServer {

  def app: Stream[IO, Kleisli[IO, Request[IO], Response[IO]]] =
    Stream.eval(IO.pure(routes.orNotFound))

  def routes: HttpRoutes[IO] = MruRoutes.helloWorldRoutes(HelloWorld.impl)

  def stream(implicit T: Timer[IO], C: ContextShift[IO]): Stream[IO, Nothing] = {
    for {
      httpApp <- app
      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      exitCode <- BlazeServerBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}