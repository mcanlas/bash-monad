package com.htmlism

import scala.util.chaining._

import cats.syntax.all._

package object bashmonad {
  implicit class BashInterpolator(sc: StringContext) {
    def bash(xs: EnvironmentVariable*): String =
      (sc.parts.head :: xs
        .zip(sc.parts.tail)
        .toList
        .flatMap(ab => List("${", ab._1.name, "}", ab._2)))
        .mkString("")
  }

  implicit def strToArg(s: String): BashArgument =
    BashArgument(s)

  implicit def rawToBash(r: Raw): BashProgram[Unit] =
    BashProgram((), r.s)

  implicit def cmdToBash(cmd: Cmd): BashProgram[Unit] = {
    val quotedLine =
      cmd
        .xs
        .map(_.s)
        .map(quote)
        .mkString(" ")

    BashProgram((), quotedLine)
  }

  private def quote(s: String) =
    s
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .pipe {
        case s if s.contains("${") =>
          "\"" + s + "\""
        case s =>
          s
      }

  implicit def args1ToBash(args: Args1): BashProgram[EnvironmentVariable] =
    validateArgs(args.xs)
      .map(xs => xs(0))

  implicit def args2ToBash(args: Args2): BashProgram[(EnvironmentVariable, EnvironmentVariable)] =
    validateArgs(args.xs)
      .map(xs => xs(0) -> xs(1))

  implicit def args3ToBash(args: Args3): BashProgram[(EnvironmentVariable, EnvironmentVariable, EnvironmentVariable)] =
    validateArgs(args.xs)
      .map(xs => (xs(0), xs(1), xs(2)))

  private def validateArgs(xs: Seq[String]) = {
    val indices =
      xs
        .toList
        .zipWithIndex
        .map { case (s, i) => s + "=$" + (i + 1) }

    val vars =
      xs
        .toList
        .map(EnvironmentVariable)

    for {
      _ <- BashProgram((), indices, Nil)

      _ <- Raw("""if
          |fi""".stripMargin)
    } yield vars
  }
}
