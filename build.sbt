lazy val root =
  Project("bash-monad", file("."))
    .aggregate(core)
    .disablePublishing

lazy val core =
  module("core")
    .settings(description := "A Scala DSL for generating Bash scripts")
    .withCats
    .withTesting
