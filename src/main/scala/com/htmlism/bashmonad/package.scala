package com.htmlism

import scala.util.chaining._

package object bashmonad {
  implicit class BashInterpolator(sc: StringContext) {
    def bash(xs: EnvironmentVariable*): BashArgument = {
      val escapedParts =
        sc
          .parts
          .map(BashArgument.quote)

      (escapedParts.head :: xs
        .zip(escapedParts.tail)
        .toList
        .flatMap { case (bVar, s) =>
          List("${", bVar.name, "}", s)
        })
        .mkString("")
        .pipe(BashArgument(_))
    }
  }

  implicit def toBashProgram[A, B](x: A)(implicit enc: BashProgramEncoder[A, B]): BashProgram[B] =
    enc.encode(x)
}
