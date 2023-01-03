package com.htmlism.bashmonad
package args

object Args extends ArgsBuilder[Args1] {
  def string(s: String): Args1 =
    Args1(List(s))

  def oneOf(s: String): Args1 =
    Args1(List(s))
}

case class Args1(xs: List[String]) extends ArgsBuilder[Args2] {
  def string(s: String): Args2 =
    Args2(xs :+ s)

  def oneOf(s: String): Args2 =
    Args2(xs :+ s)
}

object Args1 {
  implicit val args1AsBash: BashProgramEncoder[Args1, EnvironmentVariable] =
    (args: Args1) =>
      validateArgs(args.xs)
        .map(xs => xs(0))
}

case class Args2(xs: List[String]) extends ArgsBuilder[Args3] {
  def string(s: String): Args3 =
    Args3(xs :+ s)

  def oneOf(s: String): Args3 =
    Args3(xs :+ s)

}

object Args2 {
  implicit val args2AsBash: BashProgramEncoder[Args2, (EnvironmentVariable, EnvironmentVariable)] =
    (args: Args2) =>
      validateArgs(args.xs)
        .map(xs => xs(0) -> xs(1))
}

case class Args3(xs: List[String])

object Args3 {
  implicit val args3AsBash: BashProgramEncoder[Args3, (EnvironmentVariable, EnvironmentVariable, EnvironmentVariable)] =
    (args: Args3) =>
      validateArgs(args.xs)
        .map(xs => (xs(0), xs(1), xs(2)))
}

private[args] trait ArgsBuilder[A] {

  /**
    * Assigns the next positional parameter to a variable
    *
    * @param s
    *   The variable name to assign
    */
  def string(s: String): A

  /**
    * TODO
    *
    * Assigns the next positional parameter to a variable, and validates it against a set of values
    *
    * @param s
    *   The variable name to assign
    */
  def oneOf(s: String): A
}
