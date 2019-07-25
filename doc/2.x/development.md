# Developing for JavaSpec

## Target Environment

Releases target [JRE 8][jre-download] or higher.  JUnit-oriented artifacts need to be compatible
with JUnit 4.

JavaSpec and its dependencies are available in the [Maven Central Repository][maven-central].

[jre-download]: https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
[maven-central]: https://search.maven.org


## Development Environment

### Java

Java sources target Java 8 and are built with [Gradle][gradle-java-plugin], which handles the build,
testing, and packaging of Java sources as is common for Java projects.

These can be installed on Homebrew systems as follows:

    brew tap caskroom/versions
    brew cask install java8

[gradle-java-plugin]: https://docs.gradle.org/current/userguide/java_plugin.html#java_plugin

### Ruby

Rake tasks and Cucumber Ruby code target the Ruby version listed in `.ruby-version`.  Something like
[`rvm`][rvm] or [`rbenv`][rbenv] is recommended to install a compatible version of Ruby.

[rbenv]: https://github.com/rbenv/rbenv
[rvm]: https://rvm.io


## Development

### Build Automation via Rake

Gradle is pretty good at automating Java-related tasks, but there are also Cucumber tests in Ruby to
consider, that test JavaSpec externally.

This is where [Rake][rake] comes in.  It offers a single place to organize and run tasks for
building, testing, and releasing sources without having to resort to scripts.  Rake tasks (which can
be listed with `rake -T`) simply delegate to Gradle, where Java sources are concerned.  The Rake
tasks attempt to be as orthogonal as possible, meaning:

* Each task does one, logical thing.
* Tasks can be run in any combination or sequence.

So if you want to run Cucumber Ruby tests against freshly-compiled Java code, you run `rake
java:compile cucumber`.  If your Java code is already compiled and you just want to run the Cucumber
Ruby tests again, it's `rake cucumber`.

[rake]: https://github.com/ruby/rake


### Style and Static Analysis (Checkstyle)

[Checkstyle][checkstyle-config] is used to check the code format and style.  Run it with `rake
checkstyle:run`.  Different configurations are used, depending upon where you are in the source
tree:

* `src/main/java` code is checked with `checkstyle-main.xml`
* `src/test/java` code is checked with `checkstyle-test.xml`.  A few of the rules are relaxed, so
  that long descriptions of behavior can be written out as Java classes and method names.

[checkstyle-config]: http://checkstyle.sourceforge.net/config.html


### Testing

There are a number of different testing tools that are used for testing, and tests are written in a
[variety of scopes][fowler-test-pyramid], or levels of abstraction.

* Run `rake java:junit` to run JUnit tests on Java sources, using the familiar `./gradlew test`
  workflow.  These offer immediate feedback on JavaSpec classes, from inside the same process.
* Run `rake java:test` to run all those tests, plus [Cucumber-jvm][cucumber-jvm] tests in
  `info.javaspecfeature`.  This runs acceptance tests for high-level - but less immediate - feedback
  on JavaSpec as a whole.  The test code still runs in the same process as the production code being
  tested, so this leaves some possible gaps for things like runtime dependencies and class-loading.
* Run `rake cucumber` or `rake cucumber:focus` to run (focused) Cucumber Ruby tests.
  These tests focus on JavaSpec's behavior when run as an external process, so it covers things like
  exit codes and console output.  Note that the `cucumber-docker` tasks can be used to run build a
  Docker image to run these tests, if an extra layer of isolation is desired.


Note that - while it is possible to write the last category of tests in Java - the author's
experiences have been that it's rather laborious to launch, monitor, and scrape output from external
processes in Java, when it's so easy in Ruby.

One final note about the way tests are organized: it's not an explicit goal to have three separate
sets of tests that roughly correspond to the levels of the Testing Pyramid.  It just sort of turned
out that way, by covering gaps the author encountered along the way, using whatever tools seemed
practical at the time.

[cucumber-jvm]: https://docs.cucumber.io/installation/java
[fowler-test-pyramid]: https://martinfowler.com/bliki/TestPyramid.html


### Continuous Integration (CI)

Continuous Integration happens on [Travis][travis-javaspec], using the [generic
image][travis-generic] that has a JDK and Ruby installed.  Dependencies are installed before the
main `script` stage, just like in the [Java image][travis-java].

Use `rake travis:lint` to run a linter on any changes to `.travis.yml`.

The build is modified slightly on the CI environment, which is detected with one of the variables
that [Travis sets][travis-environment]:

* Gradle runs with a `plain` console, so that it doesn't garble the output.
* Gradle `testLogging` shows some events, so there's a record of which tests ran and what their
  results were.

[travis-environment]: https://docs.travis-ci.com/user/environment-variables/#default-environment-variables
[travis-generic]: https://docs.travis-ci.com/user/languages/minimal-and-generic/#generic
[travis-java]: https://docs.travis-ci.com/user/languages/java/#gradle-dependency-management 
[travis-javaspec]: https://travis-ci.org/kkrull/javaspec


## Releasing

Rake also has tasks to automate the process of building and uploading a release.  This was as
semi-automated process at best, even in its first incarnation as Bash scripts, and it would benefit
from further refinement.  Old notes guiding the process are in [the outdated release
document](../1.0.1/release.md).

It relies upon tools such as [GPG][gpg] which will need to be installed, configured, and documented
here.

It also attempts to deploy JavaSpec to [Sonatype OSS][sonatype], which may not even exist or offer
free accounts anymore, as far as the author knows.

[gpg]: https://gpgtools.org
[sonatype]: https://www.sonatype.com


### Artifacts

Speaking of artifacts: _which JARs get released?_

That's a great question.  JavaSpec is currently under development to add an additional, Mocha-like
syntax (while retaining the MSpec-inspired syntax from the 1.x series) as well as an external test
runner.  This will likely result in more artifacts, that still need some further iteration before we
can solve the problem of independent use without creating the problem of a mess of poorly-conceived
JARs.
