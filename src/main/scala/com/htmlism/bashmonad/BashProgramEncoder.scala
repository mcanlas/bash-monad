package com.htmlism.bashmonad

trait BashProgramEncoder[A, B] {
  def encode(x: A): BashProgram[B]
}
