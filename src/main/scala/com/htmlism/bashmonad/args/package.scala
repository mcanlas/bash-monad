package com.htmlism.bashmonad

package object args {
  private[args] def validateArgs(xs: List[String]) = {
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
