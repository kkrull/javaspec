#!/usr/bin/env bash

set -e

scripts_dir=$(greadlink -f "$(dirname -- "${BASH_SOURCE[0]}")")
project_dir=$(greadlink -f "$scripts_dir/..")

lib_dir="$project_dir/lib"
junit_console_jar="$lib_dir/junit-platform-console-standalone-1.8.1.jar"

#Make sure engine jar is up to date: ./gradlew :javaspec-engine:assemble && ./scripts/junit-console.sh
#Remember --select-class=info.javaspec.client.GreeterSpecs, too (or it won't discover any specs)
cd "$project_dir"
java -jar "$junit_console_jar" \
  --classpath=javaspec-api/build/classes/java/main \
  --classpath=javaspec-client/build/classes/java/main \
  --classpath=javaspec-client/build/classes/java/test \
  --classpath=javaspec-engine/build/libs/javaspec-engine-0.0.1.jar \
  --include-engine=javaspec-engine \
  $@
