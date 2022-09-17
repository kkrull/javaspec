# Git Hooks

There's a [pre-commit hook][git-custom-hooks] that verifies that the code is
formatted.  It will stop the commit and tell you about any violations, if any
code is not formatted correctly.

Install it by copying the hook to your `.git` directory in this repository.
There is a Gradle task that automates this:

```shell
$ ./gradlew installGitHook
```

[git-custom-hooks]: https://git-scm.com/book/en/v2/Customizing-Git-Git-Hooks
