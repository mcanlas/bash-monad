package com.htmlism.bashmonad

object Encoder {
  def toLines(x: BashProgram[_]): List[String] =
    interFlatMap(x.flatten)(List(""), _.lines)

  def interFlatMap[A, B](xs: List[A])(x: List[B], f: A => List[B]): List[B] =
    xs match {
      case head :: tail =>
        f(head) ::: tail.flatMap(a => x ::: f(a))

      case Nil =>
        Nil
    }
}
