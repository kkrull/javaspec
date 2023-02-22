# Java Development Kit (JDK)

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
