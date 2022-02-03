#!/usr/bin/env bash

set -e

junit_console_jar='/usr/local/opt/java/junit-platform-console-standalone-1.8.1.jar'
java -jar "$junit_console_jar" \
  --classpath=javaspec-api/build/classes/java/main \
  --classpath=javaspec-client/build/classes/java/test \
  --select-class=info.javaspec.client.GreeterSpecs \
  $@
