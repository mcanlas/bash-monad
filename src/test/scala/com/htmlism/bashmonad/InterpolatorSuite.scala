package com.htmlism.bashmonad

import weaver._

object InterpolatorSuite extends FunSuite {
  test("bash string interpolator exists") {
    expect.eql("hello world", bash"hello world".s)
  }

  test("environment variables are braced") {
    val envFoo =
      EnvironmentVariable("foo")

    expect.eql("hello ${foo} world", bash"hello $envFoo world".s)
  }

  test("slashes and quotes are escaped") {
    val envFoo =
      EnvironmentVariable("foo")

    expect.eql("""\\ \" ${foo} \\ \"""", bash"""\ " $envFoo \ """".s)
  }
}
