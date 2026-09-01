package com.htmlism

import scala.util.chaining.*

package object bashmonad {
  val Args =
    args.Args

  implicit class BashInterpolator(sc: StringContext) {
    def bash(xs: EnvironmentVariable*): BashArgument = {
      val escapedParts =
        sc
          .parts
          .map(BashArgument.quote)
          .toList

      escapedParts match {
        case head :: tail =>
          (head :: xs
            .zip(tail)
            .toList
            .flatMap { case (bVar, s) =>
              List("${", bVar.name, "}", s)
            })
            .mkString("")
            .pipe(BashArgument(_))

        case Nil =>
          BashArgument("")
      }
    }
  }

  implicit def toBashProgram[A, B](x: A)(implicit enc: BashProgramEncoder[A, B]): BashProgram[B] =
    enc.encode(x)
}
