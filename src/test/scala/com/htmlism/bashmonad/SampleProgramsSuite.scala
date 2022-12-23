package com.htmlism.bashmonad

import cats.syntax.all._
import weaver._

object SampleProgramsSuite extends FunSuite {
  test("args one") {
    val prog =
      for {
        _ <- Args("abc")
      } yield ()

    val expected =
      """abc=$1""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("args two") {
    val prog =
      for {
        _ <- Args("abc", "def")
      } yield ()

    val expected =
      """abc=$1
        |def=$2""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("args three") {
    val prog =
      for {
        _ <- Args("abc", "def", "xyz")
      } yield ()

    val expected =
      """abc=$1
        |def=$2
        |xyz=$3""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("raw command") {
    val prog =
      for {
        _ <- Raw("ls -la foo bar")
      } yield ()

    val expected =
      """ls -la foo bar""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("quoted command") {
    val prog =
      for {
        _ <- BashProgram.cmd("ls", "-la", "foo", "bar")
      } yield ()

    val expected =
      """ls -la foo bar""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("quoted command, escapes") {
    val prog =
      for {
        _ <- BashProgram.cmd("echo", "\\", "\"")
      } yield ()

    val expected =
      """echo \\ \"""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }
}
