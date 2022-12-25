package com.htmlism.bashmonad

import scala.annotation.tailrec

//import scala.util.chaining._

final case class MultiLineCommand(xs: List[String], options: List[(String, Option[BashArgument])]) {
  def apply(ys: String*): MultiLineCommand =
    copy(xs = xs ++ ys.toList)

  def opt(s: String): MultiLineCommand =
    copy(options = options :+ (s, None))

  def opt(s: String, arg: BashArgument): MultiLineCommand =
    copy(options = options :+ (s, Some(arg)))
}

object MultiLineCommand {
  implicit val multiAsBash: BashProgramEncoder[MultiLineCommand, Unit] =
    (multi: MultiLineCommand) => {
      val head =
        multi.xs.mkString(" ")

      val indents =
        multi
          .options
          .flatMap { case (k, ov) =>
            val indentedKey =
              List("  --" + k)

            val indentedValue =
              ov.map("    " + _.s).toList

            indentedKey ++ indentedValue
          }

      BashProgram(interSlash(head :: indents, Nil))
    }

  def apply(xs: String*): MultiLineCommand =
    MultiLineCommand(xs.toList, Nil)

  @tailrec
  def interSlash(xs: List[String], outs: List[String]): List[String] =
    xs match {
      case Nil =>
        outs

      case head :: Nil =>
        outs :+ head

      case head :: tail =>
        interSlash(tail, outs :+ (head + " \\"))
    }
}
