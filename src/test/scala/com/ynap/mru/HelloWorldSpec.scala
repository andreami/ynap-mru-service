package com.ynap.mru

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import org.scalatest.FunSuite

class HelloWorldSpec extends FunSuite {

  test("hello world") {
    val response = retHelloWorld
    val body = response.as[String].unsafeRunSync()

    assert(response.status == Status.Ok)
    assert(body == "{\"message\":\"Hello, world\"}")
  }

  private[this] val retHelloWorld: Response[IO] = {
    val getHW = Request[IO](Method.GET, uri"/hello/world")
    val helloWorld = HelloWorld.impl[IO]
    MruRoutes.helloWorldRoutes(helloWorld).orNotFound(getHW).unsafeRunSync()
  }
}