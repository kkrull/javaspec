## Update version at the start of a release branch

- Bump the artifact version number in the POM.
- Bump the expected, reportable version number in `CommandLineInterfaceSteps`.
- When the test fails, bump the actual reportable version in `JavaSpec`.

## Release start

- Create a release branch off of master.
- Bump the version to `-SNAPSHOT`.
- Merge in features and bug fixes: Use develop if all topics are desired; specific topic branches for a subset.
  **Remember to use `git merge --no-ff`** to make development history more clear.

## Release end
### Test

- Drop SNAPSHOT from version
- Run tests
- Smoke test w/ a completely separate project

### Documentation

- Update README
- Push README to github page for use on [JavaSpec.info](http://javaspec.info)

### Deployment

- Push to Sonatype
- Delete local artifact and do test installation from Maven Central
