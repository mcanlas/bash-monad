package com.htmlism.bashmonad

import cats._
import cats.syntax.all._
import weaver._

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
            BashProgram("".asRight[Int], Nil, None)
          case n =>
            BashProgram((n - 1).asLeft[String], Nil, None)
        }
        .flatten

    expect.eql(3, countingDown.size)
  }
}
