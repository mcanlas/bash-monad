package com.htmlism.bashmonad

import scala.util.chaining._

final case class Cmd(xs: BashArgument*) extends AnyVal

object Cmd {
  implicit val cmdAsBash: BashProgramEncoder[Cmd, Unit] =
    (cmd: Cmd) =>
      cmd
        .xs
        .map(_.s)
        .mkString(" ")
        .pipe(BashProgram(_))
}
