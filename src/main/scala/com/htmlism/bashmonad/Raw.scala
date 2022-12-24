package com.htmlism.bashmonad

/**
  * Use to construct a command that doesn't use any quoting
  */
final case class Raw(s: String) extends AnyVal

object Raw {
  implicit val rawAsBash: BashProgramEncoder[Raw, Unit] =
    (r: Raw) => BashProgram(r.s)
}
