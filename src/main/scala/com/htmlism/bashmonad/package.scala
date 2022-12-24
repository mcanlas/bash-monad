package com.htmlism

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
}
