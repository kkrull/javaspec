# JavaSpec for JUnit Jupiter

The next version of JavaSpec.

* [Problem statement](./docs/problems.md)


## Development

### Git

There's a pre-commit hook that verifies that the code is formatted.  It will tell you how to address
any violations.

Install it by copying the hook to your `.git` directory in this repository.  From the repository
root:

    $ cp nested-lambdas/etc/git-hooks/* .git/hooks

Or use easy mode:

    $ ./gradlew installGitHook


### Gradle and Java JDKs

Development uses Gradle 7 and OpenJDK 11 LTS.  Gradle has recently been a bit
finicky about which JDK it seems happy to use, so it is best to refer to the
[Gradle-Java compatibility matrix](https://docs.gradle.org/current/userguide/compatibility.html)
when upgrading.

Installing and using the JDK is no longer as straightforward as it once was,
either.  As of the time of this writing, OpenJDK 11 is the latest LTS release of
Java.

* Install the JDK on MacOS via Homebrew by adding a tap,

  ```shell
  $ brew tap homebrew/cask
  $ brew tap homebrew/cask-versions
  $ brew search jdk #Should list openjdk@11
  ```

* installing a version-specific formula and an environment manager
  [`jenv`](https://www.jenv.be/),

  ```shell
  $ brew install openjdk@11
  $ brew install jenv
  $ #Follow steps to configure your shell to use jenv
  $ jenv doctor #Make sure jenv is happy
  ```

* configuring the system to use that JDK,

  ```shell
  $ jenv add <path to OpenJDK 11>
  $ jenv versions
  $ jenv local 11.0.12
  ```

* and verifying that the project is using that version

  ```shell
  $ ./gradlew -v
  ```


### IntelliJ IDEA

Set the test runner in (Preferences | Build, Execution, Deployment | Build Tools | Gradle) to "IntelliJ IDEA", instead
of the default "Gradle", in order to show `@DisplayName` properly, from JUnit Jupiter tests.
