# Developing for JavaSpec

TODO KDK: Update documentation

- Introduction in general
- What you need for development: JDK, Ruby
- Which module does what
- Refactor to use Rake
- Add contributor's guide
- CI


## Make a release branch

- Make a release branch from develop: `git checkout develop && git checkout -b release-v<number>`
- Bump the artifact version number in the POM: `bin/bump-version`
- Bump the expected, reportable version number in `CommandLineInterfaceSteps`
- Bump the version in the installation instructions in `readme.md`


## Release end
### Test

- Run tests
- Smoke test w/ a completely separate project


### Documentation

- Update README
  - Version number in Maven coordinates
  - Version history for the new version
- Push README to github page for use on [JavaSpec.info](http://javaspec.info)


### Git

- Merge release branch into master **`no-ff`**.
- Tag master with new version number.
- Merge release branch into develop, with fast-forward.
- Push tags and branches.
- Delete release branch.

```
git checkout master && git merge --no-ff <release_branch>
git tag <version_number>
git checkout develop && git merge <release_branch>
```

### Deployment

The overall process is described [here](http://central.sonatype.org/pages/ossrh-guide.html#releasing-to-central).

- Get a GPG key ready
  * Install `gnupg` in cygwin.  Make a key and upload it.
  * `gpg --gen-key`
  * `gpg --keyserver keyserver.ubuntu.com --send-keys <key id>`
  * Set up a [profile with the GPG pass phrase](https://maven.apache.org/plugins/maven-gpg-plugin/usage.html) in `~/.m2/settings.xml`

```
<profile>
  <id>gpg</id>
  <properties>
    <gpg.passphrase>goes-here</gpg.passphrase>
  </properties>
</profile>
```

- `mvn -Pgpg,release clean install` should now work (`./bin/build-for-deployment`).
- Set up [credentials for OSSRH](http://central.sonatype.org/pages/apache-maven.html#distribution-management-and-authentication)
  in `~/.m2/settings.xml`

```
<server>
  <id>ossrh</id>
  <username>goes-here</username>
  <password>goes-here</password>
</server>
```

- Push to Sonatype: `mvn -Pgpg,release deploy` (`./bin/deploy`).
- Log in to [Sonatype](https://oss.sonatype.org/) in a browser, and find the staging repository (search).
- Check the contents tab to make sure the compiled jar, source, javadocs and GPG keys are all present.
- [Close](http://central.sonatype.org/pages/releasing-the-deployment.html#close-and-drop-or-release-your-staging-repository) the repository.
- Release the repository.  Now the staging repository should be gone.
- Go to [Maven Central](http://search.maven.org/#search|ga|1|g%3A%22info.javaspec%22) and make sure it shows up.  It's
  supposed to take about 10 minutes to be in the repository, and up to 2 hours to show up in search results.
- Delete local artifact and do test installation from Maven Central.
