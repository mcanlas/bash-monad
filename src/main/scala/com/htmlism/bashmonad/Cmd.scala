package com.htmlism.bashmonad

import scala.util.chaining._

final case class Cmd(xs: BashArgument*) extends AnyVal

object Cmd {
  implicit val cmdAsBash: BashProgramEncoder[Cmd, Unit] =
    (cmd: Cmd) =>
      cmd
        .xs
        .map(_.s)
        .map(quote)
        .mkString(" ")
        .pipe(BashProgram(_))

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
