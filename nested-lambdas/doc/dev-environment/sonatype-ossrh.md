# Sonatype OSSRH Repository

Artifacts are hosted on Sonatype's [OSSRH repository][sonatype-nexus], which
provides a way to distribute SNAPSHOT artifacts and publish release artifacts to
the Maven Central Repository.

If you are publishing artifacts, you need a Sonatype OSSRH account.  Get one by
following the instructions in the
[OSSRH Getting Started Guide][sonatype-publish-guide].

Once completed, you will need to define the following properties at runtime
(either in `$HOME/.gradle/gradle.properties` or as `-D` system properties):

```ini
#$HOME/.gradle/gradle.properties
sonatype.username=...
sonatype.password=...
```

[sonatype-nexus]: https://oss.sonatype.org/
[sonatype-publish-guide]: https://central.sonatype.org/publish/publish-guide/
