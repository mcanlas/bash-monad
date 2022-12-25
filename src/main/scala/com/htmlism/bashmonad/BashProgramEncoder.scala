package com.htmlism.bashmonad

/**
  * A mechanism to allow any data types to be compiled into Bash scripts, without the need for one type hierarchy
  *
  * @tparam A
  *   The value to be encoded as a Bash fragment
  * @tparam B
  *   The payload type of the Bash monad
  */
trait BashProgramEncoder[A, B] {
  def encode(x: A): BashProgram[B]
}
