package com.htmlism.bashmonad

final case class SetVar(key: String, value: BashArgument)

object SetVar {
  implicit val varAsBash: BashProgramEncoder[SetVar, EnvironmentVariable] =
    (set: SetVar) => BashProgram(EnvironmentVariable(set.key), List(set.key + "=" + set.value.s), Nil)
}
