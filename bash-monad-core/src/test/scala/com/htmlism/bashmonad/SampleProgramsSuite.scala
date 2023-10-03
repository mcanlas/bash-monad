package com.htmlism.bashmonad

import weaver.*

object SampleProgramsSuite extends FunSuite {
  test("args one") {
    val prog =
      for {
        abc <- Args.string("abc")

        _ <- Cmd("echo", abc)
      } yield ()

    val expected =
      """if [ $# -ne 1 ]; then
        |  echo "Usage: $0 <abc>"
        |  exit 1
        |fi
        |
        |abc=$1
        |
        |echo "$abc"""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("args two") {
    val prog =
      for {
        _ <- Args.string("abc").string("def")
      } yield ()

    val expected =
      """if [ $# -ne 2 ]; then
        |  echo "Usage: $0 <abc> <def>"
        |  exit 1
        |fi
        |
        |abc=$1
        |def=$2""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("args three") {
    val prog =
      for {
        _ <- Args.string("abc").string("def").string("xyz")
      } yield ()

    val expected =
      """if [ $# -ne 3 ]; then
        |  echo "Usage: $0 <abc> <def> <xyz>"
        |  exit 1
        |fi
        |
        |abc=$1
        |def=$2
        |xyz=$3""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("setting a variable") {
    val prog =
      for {
        abc <- SetVar("abc", "123")
      } yield abc

    val expected =
      """abc=123""".stripMargin

    expect.same(EnvironmentVariable("abc"), prog.x) and
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
        _ <- Cmd("ls", "-la", "foo", "bar")
      } yield ()

    val expected =
      """ls -la foo bar""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("quoted command, simple escapes") {
    val prog =
      for {
        _ <- Cmd("echo", "\\", "\"")
      } yield ()

    val expected =
      """echo \\ \"""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("quoted command, direct variable use") {
    val prog =
      for {
        abc <- SetVar("abc", "123")

        _ <- Cmd("echo", abc)
      } yield ()

    val expected =
      """abc=123
        |
        |echo "$abc"""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }

  test("multi-line command") {
    val prog =
      for {
        _ <- MultiLineCommand("aws", "lambda")("list-tags")
          .opt("hello")
          .opt("hello-world", "foo")
      } yield ()

    val expected =
      """aws lambda list-tags \
        |  --hello \
        |  --hello-world \
        |    foo""".stripMargin

    expect.eql(expected, Encoder.encode(prog))
  }
}
