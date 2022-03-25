#!/usr/bin/env zsh

set -e

scripts_dir="${0:a:h}"
project_dir="$(realpath "$scripts_dir/..")"

lib_dir="$project_dir/lib"
junit_console_jar="$lib_dir/junit-platform-console-standalone-1.8.1.jar"

cd "$project_dir"

engine_with_service_descriptor='javaspec-engine/build/libs/javaspec-engine-0.0.1.jar'
test_sources='javaspec-engine/build/classes/java/test'
test_sources_dependencies=$(./gradlew -q :javaspec-engine:printTestRuntimeClasspath)

java -jar "$junit_console_jar" \
  --classpath="$engine_with_service_descriptor" \
  --classpath="$test_sources" \
  --classpath="$test_sources_dependencies" \
  --include-engine=javaspec-engine \
  --select-class=info.javaspec.engine.JavaSpecEngineTest \
  $@
