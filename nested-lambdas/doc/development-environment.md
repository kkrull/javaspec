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

Installing and using the JDK is no longer as straightforward as it once was.  As
of the time of this writing, OpenJDK 11 is the latest LTS release of Java.

Homebrew users will need to add a tap first, before installing OpenJDK.

```shell
# Install the tap
$ brew tap homebrew/cask
$ brew tap homebrew/cask-versions
$ brew search jdk #Should list openjdk@11

# Install the JDK
$ brew install openjdk@11
```


## Manage your Java environment with `jenv` (recommended)

If you have multiple JDKs installed on your system, it's easy to get mixed up
and use a different version of the JDK than is used to develop this project.  An
environment manager like [`jenv`](https://www.jenv.be/) can help.  It sets
environment variables like `JAVA_HOME` to the version specified in
`.java-version`.

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


## Use `gradle` for just about everything else

This project uses Gradle in the traditional fashion by using the included
scripts (`gradlew` and `gradlew.bat`).  These scripts automatically download and
use a pinned version of Gradle that is known to be compatible with the code and
the configured JDK.

There are a couple of things that come in handy, if you are new to Gradle:

```shell
# Verify your Gradle installation
$ ./gradlew -v

# List available tasks
$ ./gradlew tasks --all
```


## Install `git` hooks to enforce standards (recommended)

There's a pre-commit hook that verifies that the code is formatted.  It will
tell you how to address any violations.

Install it by copying the hook to your `.git` directory in this repository.
From the repository root:

```shell
$ cp nested-lambdas/etc/git-hooks/* .git/hooks
```

Or use this Gradle task to do it for you:

```shell
$ ./gradlew installGitHook
```
