package com.ynap.mru

import cats.effect._
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    MruServer.stream.compile.drain.as(ExitCode.Success)
}