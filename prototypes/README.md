# JUnit5 Prototypes for JavaSpec

Prototypes to figure out how the next version of JavaSpec might work.

* [Problem statement](./docs/problems.md)


## Development

### Gradle and Java JDKs

Development uses Gradle 7 and OpenJDK 11 LTS.  Gradle has recently been a bit
finicky about which JDK it seems happy to use, so it is best to refer to the
[Gradle-Java compatibility matrix](https://docs.gradle.org/current/userguide/compatibility.html)
when upgrading.

Installing the JDK is no longer as straightforward as it once was, either.  As
of the time of this writing, OpenJDK 11 is the latest LTS release of Java.

* Installing it on MacOS via Homebrew requires adding a tap,

  ```shell
  $ brew tap homebrew/cask
  $ brew tap homebrew/cask-versions
  $ brew search jdk #Should list openjdk@11
  ```

* installing a version-specific formula,

  ```shell
  $ brew install openjdk@11
  ```

* configuring the system to use that JDK (TBD),
* and verifying that everyone is using that version

  ```shell
  $ ./gradlew javaVersionsInstalled
  $ ./gradlew -v
  ```


### IntelliJ IDEA

Set the test runner in (Preferences | Build, Execution, Deployment | Build Tools | Gradle) to "IntelliJ IDEA", instead
of the default "Gradle", in order to show `@DisplayName` properly, from JUnit Jupiter tests.
