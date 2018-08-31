#!/bin/bash

set -e

main()
{
  case "${1}" in
    "idle")
      exec tail -f "/dev/null"
      ;;
    "tests")
      exec scala \
        -classpath "/usr/lib/examples/tests.jar" \
        "org.scalatest.tools.Runner" \
          -R  "/usr/lib/examples/tests.jar" \
          -o
      ;;
  esac
}

main "${@}"
