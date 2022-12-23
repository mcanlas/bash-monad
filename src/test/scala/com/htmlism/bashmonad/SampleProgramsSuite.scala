package com.htmlism.bashmonad

import cats.syntax.all._
import weaver._

object SampleProgramsSuite extends FunSuite {
  test("args one") {
    val prog =
      for {
        _ <- BashProgram.args("abc")
      } yield ()

    val expected =
      """abc=$1""".stripMargin

    expect.eql(expected, Encoder.toLines(prog).mkString("\n"))
  }

  test("args two") {
    val prog =
      for {
        _ <- BashProgram.args("abc", "def")
      } yield ()

    val expected =
      """abc=$1
        |def=$2""".stripMargin

    expect.eql(expected, Encoder.toLines(prog).mkString("\n"))
  }

  test("args three") {
    val prog =
      for {
        _ <- BashProgram.args("abc", "def", "xyz")
      } yield ()

    val expected =
      """abc=$1
        |def=$2
        |xyz=$3""".stripMargin

    expect.eql(expected, Encoder.toLines(prog).mkString("\n"))
  }
}
