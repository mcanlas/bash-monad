package com.htmlism.bashmonad

import scala.annotation.tailrec

import cats.*

final case class BashProgram[+A](x: A, lines: List[String], history: List[BashProgram[?]]) {
  def map[B](f: A => B): BashProgram[B] =
    copy(x = f(x))

  def flatMap[B](f: A => BashProgram[B]): BashProgram[B] = {
    val fb =
      f(x)

    fb
      .copy(history = this.history ::: this :: fb.history)
  }

  def flatten: List[BashProgram[?]] =
    history :+ this
}

object BashProgram {
  implicit val bashMonad: Monad[BashProgram] =
    new Monad[BashProgram] {
      def pure[A](x: A): BashProgram[A] =
        BashProgram(x, Nil, Nil)

      def flatMap[A, B](fa: BashProgram[A])(f: A => BashProgram[B]): BashProgram[B] =
        fa
          .flatMap(f)

      def tailRecM[A, B](a: A)(f: A => BashProgram[Either[A, B]]): BashProgram[B] = {
        @tailrec
        def recur(res: BashProgram[Either[A, B]], agg: List[BashProgram[?]]): BashProgram[B] =
          res.x match {
            case Left(a) =>
              val fb =
                f(a)

              recur(fb, fb :: agg)

            case Right(b) =>
              pure(b)
                .copy(history = agg)
          }

        recur(f(a), Nil)
      }
    }

  def apply(xs: List[String]): BashProgram[Unit] =
    BashProgram((), xs, Nil)

  def apply(xs: String*): BashProgram[Unit] =
    BashProgram(xs.toList)
}
