package com.htmlism.bashmonad

case class EnvironmentVariable(name: String)

object EnvironmentVariable {
  implicit def varToBashArg(envVar: EnvironmentVariable): BashArgument =
    BashArgument("\"$" + envVar.name + "\"")
}
