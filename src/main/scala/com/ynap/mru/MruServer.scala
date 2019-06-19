package com.ynap.mru

import cats.effect._
import cats.implicits._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import fs2.Stream

import scala.concurrent.ExecutionContext.global

object MruServer {

  def stream(implicit T: Timer[IO], C: ContextShift[IO]): Stream[IO, Nothing] = {
    for {
      client <- BlazeClientBuilder[IO](global).stream
      helloWorldAlg = HelloWorld.impl[IO]
      jokeAlg = Jokes.impl[IO](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        MruRoutes.helloWorldRoutes[IO](helloWorldAlg) <+>
        MruRoutes.jokeRoutes[IO](jokeAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}