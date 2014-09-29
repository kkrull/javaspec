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

### Git

- Merge release branch into master **`no-ff`**
- Merge release branch into develop
- Delete release branch

### Deployment

The overall process is described [here](http://central.sonatype.org/pages/ossrh-guide.html#releasing-to-central).

- Push to Sonatype: `./bin/mvn-sonatype`.  This will require a GPG key.
- Log in to [Sonatype](https://oss.sonatype.org/) in a browser, and find the staging repository (search).
- Check the contents tab to make sure the compiled jar, source, javadocs and GPG keys are all present.
- [Close](http://central.sonatype.org/pages/releasing-the-deployment.html#close-and-drop-or-release-your-staging-repository) the repository.
- Release the repository.  Now the staging repository should be gone.
- Go to [Maven Central](http://search.maven.org/#search|ga|1|g%3A%22info.javaspec%22) and make sure it shows up.  It's
  supposed to take about 10 minutes to be in the repository, and up to 2 hours to show up in search results.
- Delete local artifact and do test installation from Maven Central.
