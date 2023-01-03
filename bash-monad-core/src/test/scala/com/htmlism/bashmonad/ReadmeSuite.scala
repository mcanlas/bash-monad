package com.htmlism.bashmonad

import weaver._

object ReadmeSuite extends FunSuite {
  object Aws {
    val Lambda =
      MultiLineCommand("aws", "lambda")
  }

  test("args one") {
    val program =
      for {
        _ <- BashProgram("#!/bin/bash")

        // seperate steps are padded with newlines
        _ <- BashProgram("set -euo pipefail")

        // positional argument extraction
        tuple <- Args
          .string("DEPLOY_REGION")
          .string("PAYLOAD_NAME")

        // downstream commands can statically depend on variables declared earlier
        (deployRegion, payloadName) = tuple

        // bash interpolator allows for proper escaping and inline use of bash variables
        // note that the type of `deployRegion` is not `String`
        _ <- Cmd("echo", bash"""deploying to "$deployRegion" \o/ woohoo!!""")

        // pretty printing for subcommand style commands
        _ <- Aws
          .Lambda("list-tags")
          .opt("foo")
          .opt("payload", payloadName)
          .opt("region", deployRegion)
      } yield ()

    val compiled =
      Encoder.encode(program)

    println(compiled)

    val expected =
      """#!/bin/bash
        |
        |set -euo pipefail
        |
        |if [ $# -ne 2 ]; then
        |  echo "Usage: $0 <DEPLOY_REGION> <PAYLOAD_NAME>"
        |  exit 1
        |fi
        |
        |DEPLOY_REGION=$1
        |PAYLOAD_NAME=$2
        |
        |echo deploying to \"${DEPLOY_REGION}\" \\o/ woohoo\!\!
        |
        |aws lambda list-tags \
        |  --foo \
        |  --payload \
        |    "$PAYLOAD_NAME" \
        |  --region \
        |    "$DEPLOY_REGION"""".stripMargin

    expect.eql(expected, compiled)
  }
}
