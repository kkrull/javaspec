# Java Environment Manager (jEnv)

Manage your Java environment with `jenv`.

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
