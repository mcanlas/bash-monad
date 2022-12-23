package com.htmlism.bashmonad

import scala.annotation.tailrec

import cats._
import cats.syntax.all._

final case class BashProgram[A](x: A, lines: List[String], parent: Option[BashProgram[_]]) {
  def flatten: List[BashProgram[_]] = {
    @tailrec
    def recur(x: BashProgram[_], all: List[BashProgram[_]]): List[BashProgram[_]] =
      x.parent match {
        case Some(p) =>
          recur(p, p :: all)
        case None =>
          all
      }

    recur(this, Nil)
  }
}

object BashProgram {
  implicit val bashMonad: Monad[BashProgram] =
    new Monad[BashProgram] {
      def pure[A](x: A): BashProgram[A] =
        BashProgram(x, Nil, None)

      def flatMap[A, B](fa: BashProgram[A])(f: A => BashProgram[B]): BashProgram[B] =
        f(fa.x)
          .copy(parent = fa.some)

      def tailRecM[A, B](a: A)(f: A => BashProgram[Either[A, B]]): BashProgram[B] = {
        @tailrec
        def recur(res: BashProgram[Either[A, B]]): BashProgram[B] =
          res.x match {
            case Left(a) =>
              recur(f(a).copy(parent = res.some))

            case Right(b) =>
              res
                .copy(x = b)
          }

        recur(f(a))
      }
    }

  private def ev(s: String) =
    EnvironmentVariable(s)

  def apply[A](x: A, xs: String*): BashProgram[A] =
    BashProgram(x, xs.toList, None)

  def args(a: String): BashProgram[EnvironmentVariable] =
    apply(
      ev(a),
      a + "=$1"
    )

  def args(a: String, b: String): BashProgram[(EnvironmentVariable, EnvironmentVariable)] =
    apply(
      (ev(a), ev(b)),
      a + "=$1",
      b + "=$2"
    )

  def args(a: String, b: String, c: String): BashProgram[(EnvironmentVariable, EnvironmentVariable, EnvironmentVariable)] =
    apply(
      (ev(a), ev(b), ev(c)),
      a + "=$1",
      b + "=$2",
      c + "=$3"
    )
}
