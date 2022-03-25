#!/usr/bin/env zsh

set -e

scripts_dir="${0:a:h}"
project_dir="$(realpath "$scripts_dir/..")"

lib_dir="$project_dir/lib"
junit_console_jar="$lib_dir/junit-platform-console-standalone-1.8.1.jar"

# Testing javaspec-engine in JUnit Console requires JARs for the API and engine, as well as compiled tests.
cd "$project_dir"
exec ./gradlew :javaspec-api:assemble :javaspec-engine:compileTestJava :javaspec-engine:assemble
