# Publish artifacts to Sonatype

Publishing to [Sonatype OSSRH][sonatype-nexus] uses the same Gradle plugins and
configuration that is needed to [publish to Maven
Local](#publish-artifacts-to-maven-local).  The conventional plugin configures a
single `sonatype` repository, which leads to creating several Gradle tasks
containing the word `Sonatype`.

Before you run the tasks, check the versions in each `build.gradle` file to make
sure it is a SNAPSHOT or a regular release, as intended.

```shell
$ ./gradlew publishAllPublicationsToSonatypeRepository
```

Running this with a SNAPSHOT version will deploy this to the [Snapshots
repository][sonatype-snapshots].  You can view the deployed artifacts on the Nexus webapp by going to:

> Views/Repositories - Repositories - Snapshots - Browse Storage - info.javaspec

If you get any error messages while deploying, remember that you need to setup a
Sonatype account and tell Gradle how to access it.  See the [environment setup
document](./development-environment.md#publish-artifacts-to-sonatype-ossrh) for
details.

[sonatype-nexus]: https://oss.sonatype.org/
[sonatype-snapshots]: https://oss.sonatype.org/content/repositories/snapshots/
