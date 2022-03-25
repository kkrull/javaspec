#!/usr/bin/env zsh

set -e

scripts_dir="${0:a:h}"
project_dir="$(realpath "$scripts_dir/..")"

lib_dir="$project_dir/lib"
junit_console_jar="$lib_dir/junit-platform-console-standalone-1.8.1.jar"

#Make sure jars are up to date: ./gradlew :javaspec-api:assemble :javaspec-engine:assemble
#Remember --select-class=info.javaspec.client.GreeterSpecs, too (or it won't discover any specs)
#JUnit Console requires an explicit class selector (which Gradle and IntelliJ seem to do automatically)
cd "$project_dir"

engine_test_runtime_classpath=$(./gradlew -q :javaspec-engine:printTestRuntimeClasspath)

java -jar "$junit_console_jar" \
  --classpath="$engine_test_runtime_classpath" \
  --classpath=javaspec-engine/build/libs/javaspec-engine-0.0.1.jar \
  --classpath=javaspec-engine/build/classes/java/test \
  --include-engine=javaspec-engine \
  --select-class=info.javaspec.engine.JavaSpecEngineTest \
  $@
