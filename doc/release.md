## Make a release branch

- Make a release branch: `git checkout master && git checkout -b release-v<number>`
- Bump the artifact version number in the POM: `rake java:bump-version`
- Bump the expected, reportable version number in `CommandLineInterfaceSteps`
- Bump the version in the installation instructions in `README.md`


## Release
### Test

- Run tests
- Smoke test w/ a completely separate project


### Documentation

- Update `README.md`
  - Version number in Maven coordinates
  - Version history for the new version
- Push `README.md` to github page for use on [JavaSpec.info](http://javaspec.info)


### Git

- Merge release branch into `master` **`no-ff`**.
- Tag `master` with new version number.
- Push tags and branches.
- Delete release branch.


    git checkout master && git merge --no-ff <release_branch>
    git tag <version_number>


### Deploy to Sonatype Staging repository

The overall process is described [here](http://central.sonatype.org/pages/ossrh-guide.html#releasing-to-central).

- Get a GPG key ready
  * Install `gnupg` in cygwin.  Make a key and upload it.
  * `gpg --gen-key`
  * `gpg --keyserver keyserver.ubuntu.com --send-keys <key id>`
  * Set up a [profile with the GPG pass phrase](https://maven.apache.org/plugins/maven-gpg-plugin/usage.html) in `~/.m2/settings.xml`

```xml
<profile>
  <id>gpg</id>
  <properties>
    <gpg.passphrase>goes-here</gpg.passphrase>
  </properties>
</profile>
```

- `rake release:build`
- Set up [credentials for OSSRH](http://central.sonatype.org/pages/apache-maven.html#distribution-management-and-authentication)
  in `~/.m2/settings.xml`

```xml
<server>
  <id>ossrh</id>
  <username>goes-here</username>
  <password>goes-here</password>
</server>
```

Finally, push to Sonatype with `rake release:deploy`.


### Promote from staging to release

- Log in to [Sonatype](https://oss.sonatype.org/) in a browser, and find the staging repository (search).
- Check the contents tab to make sure the compiled jar, source, javadocs and GPG keys are all present.
- [Close](http://central.sonatype.org/pages/releasing-the-deployment.html#close-and-drop-or-release-your-staging-repository) the repository.
- Release the repository.  Now the staging repository should be gone.
- Go to [Maven Central](http://search.maven.org/#search|ga|1|g%3A%22info.javaspec%22) and make sure it shows up.  It's
  supposed to take about 10 minutes to be in the repository, and up to 2 hours to show up in search results.
- Delete local artifact and do test installation from Maven Central.
