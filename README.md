# bash-monad

Exploring Bash script generation with a Scala DSL

## Elsewhere

A `BashProgram[_]` feels isomorphic to a [`Writer[_]` monad](https://typelevel.org/cats/datatypes/writer.html) whose logging type is `List[String]` 
