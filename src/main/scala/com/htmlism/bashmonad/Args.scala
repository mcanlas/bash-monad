package com.htmlism.bashmonad

object Args {
  def apply(a: String): BashProgram[EnvironmentVariable] =
    BashProgram(
      ev(a),
      a + "=$1"
    )

  def apply(a: String, b: String): BashProgram[(EnvironmentVariable, EnvironmentVariable)] =
    BashProgram(
      (ev(a), ev(b)),
      a + "=$1",
      b + "=$2"
    )

  def apply(
      a: String,
      b: String,
      c: String
  ): BashProgram[(EnvironmentVariable, EnvironmentVariable, EnvironmentVariable)] =
    BashProgram(
      (ev(a), ev(b), ev(c)),
      a + "=$1",
      b + "=$2",
      c + "=$3"
    )

  private def ev(s: String) =
    EnvironmentVariable(s)
}
