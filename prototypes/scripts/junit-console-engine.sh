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
java -jar "$junit_console_jar" \
  --classpath=javaspec-api/build/libs/javaspec-api-0.0.1.jar \
  --classpath=javaspec-engine/build/libs/javaspec-engine-0.0.1.jar \
  --classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.junit.platform/junit-platform-testkit/1.8.2/43c593ad99a975588d56b501fd4353065facebfc/junit-platform-testkit-1.8.2.jar \
  --classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.assertj/assertj-core/3.22.0/c300c0c6a24559f35fa0bd3a5472dc1edcd0111e/assertj-core-3.22.0.jar \
  --classpath=javaspec-engine/build/classes/java/test \
  --include-engine=javaspec-engine \
  --select-class=info.javaspec.engine.JavaSpecEngineTest \
  $@

#TODO KDK: Use gradle to print out the testRuntimeClasspath
#./gradlew :javaspec-engine:printTestRuntimeClasspath
#--classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.junit.platform/junit-platform-testkit/1.8.2/43c593ad99a975588d56b501fd4353065facebfc/junit-platform-testkit-1.8.2.jar
#--classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.junit.platform/junit-platform-launcher/1.8.2/c334fcee82b81311ab5c426ec2d52d467c8d0b28/junit-platform-launcher-1.8.2.jar
#--classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.junit.platform/junit-platform-engine/1.8.2/b737de09f19864bd136805c84df7999a142fec29/junit-platform-engine-1.8.2.jar
#--classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.junit.jupiter/junit-jupiter-params/5.8.2/ddeafe92fc263f895bfb73ffeca7fd56e23c2cce/junit-jupiter-params-5.8.2.jar
#--classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.junit.jupiter/junit-jupiter-api/5.8.2/4c21029217adf07e4c0d0c5e192b6bf610c94bdc/junit-jupiter-api-5.8.2.jar
#--classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.junit.platform/junit-platform-commons/1.8.2/32c8b8617c1342376fd5af2053da6410d8866861/junit-platform-commons-1.8.2.jar
#--classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.junit.jupiter/junit-jupiter/5.8.2/5a817b1e63f1217e5c586090c45e681281f097ad/junit-jupiter-5.8.2.jar
#--classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.junit.jupiter/junit-jupiter-engine/5.8.2/c598b4328d2f397194d11df3b1648d68d7d990e3/junit-jupiter-engine-5.8.2.jar
#--classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.assertj/assertj-core/3.22.0/c300c0c6a24559f35fa0bd3a5472dc1edcd0111e/assertj-core-3.22.0.jar
#--classpath=$HOME/.gradle/caches/modules-2/files-2.1/org.opentest4j/opentest4j/1.2.0/28c11eb91f9b6d8e200631d46e20a7f407f2a046/opentest4j-1.2.0.jar
