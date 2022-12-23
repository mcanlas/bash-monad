package com.htmlism.bashmonad

import scala.annotation.tailrec
import scala.util.chaining._

import cats._
import cats.syntax.all._

final case class BashProgram[A](x: A, lines: List[String], parent: Option[BashProgram[_]]) {
  def map[B](f: A => B): BashProgram[B] =
    copy(x = f(x))

  def flatMap[B](f: A => BashProgram[B]): BashProgram[B] =
    f(x)
      .copy(parent = this.some)

  def flatten: List[BashProgram[_]] = {
    @tailrec
    def recur(x: BashProgram[_], all: List[BashProgram[_]]): List[BashProgram[_]] =
      x.parent match {
        case Some(p) =>
          recur(p, p :: all)
        case None =>
          this :: all
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
        fa
          .flatMap(f)

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

  def apply[A](x: A, xs: String*): BashProgram[A] =
    BashProgram(x, xs.toList, None)

  def cmd(xs: String*): BashProgram[Unit] = {
    val quotedLine =
      xs
        .map(quote)
        .mkString(" ")

    BashProgram((), quotedLine)
  }

  private def quote(s: String) =
    s
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .pipe {
        case s if s.contains("${") =>
          "\"" + s + "\""
        case s =>
          s
      }
}
