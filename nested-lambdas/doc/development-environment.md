# JavaSpec: Setting up Your Development Environment

Details how to set up your machine so that you can do development on JavaSpec.

See the sections below for details on how to install the development tools that
are used on this project and how to use them.


## A Note about Gradle-JDK compatibility

Gradle has recently been a bit finicky about which JDK it seems happy to use, so
it is best (for now) to use the versions noted in this guide.  The [Gradle-Java
compatibility matrix][gradle-compatibility] may be helpful if you need to use
different versions or if you are performing an upgrade.

[gradle-compatibility]: https://docs.gradle.org/current/userguide/compatibility.html


## Start by installing a compatible Java Development Kit (JDK)

Installing a Java Development Kit (JDK) is a little more complex than it used to
be.  There are official versions from Oracle that require subscriptions, and
there are a number of free, open-source alternatives that implement the same
standards.  This project uses one of the free, open-source varieties:
[Adoptium's OpenJDK 11 LTS][adoptium-releases].

Homebrew users will need to add a tap first, before installing the JDK:

```shell
# Install the tap
$ brew tap homebrew/cask
$ brew tap homebrew/cask-versions
$ brew search jdk #Should list openjdk@11

# Install the JDK
$ brew install openjdk@11
```

[adoptium-releases]: https://adoptium.net/temurin/releases


## Manage your Java environment with `jenv` (recommended)

If you have multiple JDKs installed on your system, it's easy to get mixed up
and use a different version of the JDK than is used to develop this project.  An
environment manager like [`jenv`][jenv] can help.  It sets environment variables
like `JAVA_HOME` to the version specified in `.java-version`.

Homebrew users can install it as follows:

```shell
# Install jenv
$ brew install jenv

# Configure jenv to register your JDK(s)
# See instructions at https://www.jenv.be/
$ jenv add ...
```

`jenv` can then tell you whether it is correctly configured:

```shell
$ jenv doctor
[OK]    JAVA_HOME variable probably set by jenv PROMPT
[OK]    Java binaries in path are jenv shims
[OK]    Jenv is correctly loaded
```

[jenv]: https://www.jenv.be/


## Use Gradle for just about everything else

This project uses [Gradle][gradle-what-is-gradle] in the traditional fashion by
using the included scripts (`gradlew` and `gradlew.bat`).  These scripts
automatically download and use a pinned version of Gradle that is known to be
compatible with the code and the configured JDK.

This project is a [multi-project build][gradle-multi-project], with configuration in the following files:

* `settings.gradle`: For the most part, this just lists the sub-projects to
  build.
* `build.gradle`: Defines tasks and plugin settings that apply to most (or all)
  sub-projects.
* `<sub-project>/build.gradle`: Defines tasks and plugin settings that apply to
  a sub-project.

There are a couple of things that come in handy, if you are new to Gradle:

```shell
# Verify your Gradle installation
$ ./gradlew -v

# List available tasks
$ ./gradlew tasks --all
```

[gradle-multi-project]: https://docs.gradle.org/current/samples/sample_building_java_applications_multi_project.html
[gradle-what-is-gradle]: https://docs.gradle.org/current/userguide/what_is_gradle.html


## Install `git` hooks to enforce standards (recommended)

There's a [pre-commit hook][git-custom-hooks] that verifies that the code is
formatted.  It will stop the commit and tell you about any violations, if any
code is not formatted correctly.

Install it by copying the hook to your `.git` directory in this repository.
There is a Gradle task that automates this:

```shell
$ ./gradlew installGitHook
```

[git-custom-hooks]: https://git-scm.com/book/en/v2/Customizing-Git-Git-Hooks
