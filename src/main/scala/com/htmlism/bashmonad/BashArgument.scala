package com.htmlism.bashmonad

import scala.util.chaining._

final case class BashArgument(s: String) extends AnyVal

object BashArgument {
  // i didn't know that putting this implicit here would work
  implicit def strToArg(s: String): BashArgument =
    BashArgument(quote(s))

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
