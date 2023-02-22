# Sign JARs with `signing` and GPG

The [signing plugin][gradle-signing-plugin] handles the details of calling GPG
to sign artifacts, including JAR files and generated POM files.

After you have installed GPG and generated a key, you can sign assemblies with:

```shell
$ ./gradlew signMavenPublication
```

You may get an error message that looks like this:

```shell
$ ./gradlew signArchives
Execution failed for task ':javaspec-api:signArchives'.
> Cannot perform signing task ':javaspec-api:signArchives' because it has no configured signatory
```

If you get an error message like that, [double-check your GPG keys and Gradle
configuration](./development-environment.md#sign-artifacts-with-gnupg).
Alternatively, you can define [provide this configuration at
runtime][gradle-signing-credentials] as follows:

```shell
$ ./gradlew signMavenPublication -Psigning.key=<ASCII armored private key> ...
```

[gradle-signing-plugin]: https://docs.gradle.org/current/userguide/signing_plugin.html
[gradle-signing-credentials]: https://docs.gradle.org/current/userguide/signing_plugin.html#sec:signatory_credentials
