#!/usr/bin/env bash

set -e

scripts_dir=$(greadlink -f "$(dirname -- "${BASH_SOURCE[0]}")")
project_dir=$(greadlink -f "$scripts_dir/..")

lib_dir="$project_dir/lib"

mkdir -p "$lib_dir"
cd "$lib_dir"
wget https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.8.1/junit-platform-console-standalone-1.8.1.jar
