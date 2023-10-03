package com.htmlism.bashmonad

import cats.*
import cats.syntax.all.*
import weaver.*

object BashMonadSuite extends FunSuite {
  test("functor syntax") {
    val res =
      for {
        n <- 123.pure[BashProgram]
      } yield n

    expect.eql(123, res.x)
  }

  test("monad syntax") {
    val res =
      for {
        n <- 123.pure[BashProgram]

        n2 <- (n + 1).pure[BashProgram]
      } yield n2

    expect.eql(124, res.x)
  }

  test("tail recursion") {
    val countingDown =
      implicitly[Monad[BashProgram]]
        .tailRecM(3) {
          case 0 =>
            BashProgram("".asRight[Int], Nil, Nil)
          case n =>
            BashProgram((n - 1).asLeft[String], Nil, Nil)
        }
        .flatten

    expect.eql(4, countingDown.size)
  }

  test("payload echo with map") {
    val prog =
      for {
        _ <- BashProgram("ls")
      } yield ()

    expect.eql("ls", Encoder.encode(prog))
  }

  test("payload echo, map and flatMap") {
    val prog =
      for {
        _ <- BashProgram("foo")

        _ <- BashProgram("bar")
      } yield ()

    expect.eql("foo\n\nbar", Encoder.encode(prog))
  }

  test("payload echo, map and flatMap, with implicit conversion") {
    val prog =
      for {
        _ <- Cmd("foo")

        _ <- Raw("bar")
      } yield ()

    expect.eql("foo\n\nbar", Encoder.encode(prog))
  }
}
