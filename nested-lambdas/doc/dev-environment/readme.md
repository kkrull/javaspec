# Development Environment

## Setup for Development

Follow these instructions-in order-to get your development environment ready for
work in this repository.

1. Install a [Java Development Kit (JDK)](./java-development-kit.md).
1. Install [Java Environment Manager (jEnv)](./jenv.md) (recommended).
1. Install [Git hooks](./git-hooks.md) to enforce standards (recommended).
1. Make sure [Gradle works](./gradle.md), as it is used for just about
   everything else.

## Setup for Deployment (optional)

There is some additional setup you need to do, if you are going to publish
artifacts.  This is not required for day to day development.

_You only need to do this setup if you are publishing to Sonatype._

1. Install [GNU Privacy Guard (`gnupg`)](./gnupg.md).
1. Configure Gradle so it can publish to [Sonatype OSSRH](./sonatype-ossrh.md).
