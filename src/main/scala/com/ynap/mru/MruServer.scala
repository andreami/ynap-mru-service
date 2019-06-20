package com.ynap.mru

import cats.implicits._
import cats.effect._
import fs2.Stream
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

object MruServer {

  def app: HttpApp[IO] = routes.orNotFound

  def routes: HttpRoutes[IO] =
    (MruRoutes.helloWorldRoutes(HelloWorld.impl) <+> MruRoutes.ping <+> MruRoutes.echo)

  def stream(implicit T: Timer[IO], C: ContextShift[IO]): Stream[IO, Nothing] = {
    for {
      httpApp <- Stream.eval(IO.pure(app))
      finalHttpApp = Logger.httpApp(true, true)(httpApp)
      exitCode <- BlazeServerBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}