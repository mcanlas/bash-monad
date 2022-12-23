package com.htmlism

import scala.util.chaining._

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

  implicit def cmdToBash(cmd: Cmd): BashProgram[Unit] = {
    val quotedLine =
      cmd
        .xs
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
}
