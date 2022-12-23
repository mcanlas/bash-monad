package com.htmlism.bashmonad

final case class Cmd(xs: BashArgument*) extends AnyVal
