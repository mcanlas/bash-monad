package com.htmlism.bashmonad

object Args {
  def string(s: String): Args1 =
    Args1(List(s))

  case class Args1(xs: List[String]) {
    def string(s: String): Args2 =
      Args2(xs :+ s)
  }

  object Args1 {
    implicit val args1AsBash: BashProgramEncoder[Args1, EnvironmentVariable] =
      (args: Args1) =>
        validateArgs(args.xs)
          .map(xs => xs(0))
  }

  case class Args2(xs: List[String]) {
    def string(s: String): Args3 =
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
    implicit val args3AsBash
        : BashProgramEncoder[Args3, (EnvironmentVariable, EnvironmentVariable, EnvironmentVariable)] =
      (args: Args3) =>
        validateArgs(args.xs)
          .map(xs => (xs(0), xs(1), xs(2)))
  }

  private def validateArgs(xs: List[String]) = {
    val indices =
      xs
        .zipWithIndex
        .map { case (s, i) => s + "=$" + (i + 1) }

    val vars =
      xs
        .map(EnvironmentVariable(_))

    val testExpr =
      "$# -ne " + xs.size.toString

    val template =
      xs
        .map(s => s"<$s>")
        .mkString(" ")

    for {
      _ <- Raw(s"""if [ $testExpr ]; then
           |  echo "Usage: $$0 $template"
           |  exit 1
           |fi""".stripMargin)

      _ <- BashProgram(indices)
    } yield vars
  }
}
