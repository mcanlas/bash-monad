package com.htmlism.bashmonad

final case class BashArgument(s: String) extends AnyVal

object BashArgument {
  // i didn't know that putting this implicit here would work
  implicit def strToArg(s: String): BashArgument =
    BashArgument(s)
}
