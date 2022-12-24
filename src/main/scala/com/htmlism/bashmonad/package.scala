package com.htmlism

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

  implicit def toBashProgram[A, B](x: A)(implicit enc: BashProgramEncoder[A, B]): BashProgram[B] =
    enc.encode(x)

  implicit def args1ToBash(args: Args1): BashProgram[EnvironmentVariable] =
    validateArgs(args.xs)
      .map(xs => xs(0))

  implicit def args2ToBash(args: Args2): BashProgram[(EnvironmentVariable, EnvironmentVariable)] =
    validateArgs(args.xs)
      .map(xs => xs(0) -> xs(1))

  implicit def args3ToBash(args: Args3): BashProgram[(EnvironmentVariable, EnvironmentVariable, EnvironmentVariable)] =
    validateArgs(args.xs)
      .map(xs => (xs(0), xs(1), xs(2)))

  private def validateArgs(xs: List[String]) = {
    val indices =
      xs
        .zipWithIndex
        .map { case (s, i) => s + "=$" + (i + 1) }

    val vars =
      xs
        .map(EnvironmentVariable)

    val testExpr =
      "$# -ne " + xs.size.toString

    val template =
      xs
        .map(s => s"<$s>")
        .mkString(" ")

    for {
      _ <- Raw(s"""if [ $testExpr ]; then
          |  echo "Usage: $$0 $template"
          |  exit 1
          |fi""".stripMargin)

      _ <- BashProgram(indices)
    } yield vars
  }
}
