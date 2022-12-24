package com.htmlism.bashmonad

object Args {
  def string(s: String): Args1 =
    Args1(List(s))
}

case class Args1(xs: List[String]) {
  def string(s: String): Args2 =
    Args2(xs :+ s)
}

case class Args2(xs: List[String]) {
  def string(s: String): Args3 =
    Args3(xs :+ s)

}

case class Args3(xs: List[String])
