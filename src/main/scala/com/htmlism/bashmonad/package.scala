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

  implicit def rawToBash(r: Raw): BashProgram[Unit] =
    BashProgram((), r.s)
}
