# Developing for JavaSpec

## Target Environment

Releases target platform running [JRE 8+][jre-download] and any project that is compatible with JUnit 4.

JavaSpec and its dependencies are available in the [Maven Central Repository][maven-central].

[jre-download]: https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
[maven-central]: https://search.maven.org/


## Development Environment

### Java

Java sources target Java 8 and are built with [Maven][maven], which handles the build, testing, and packaging of Java
sources as is common for Java projects.

These can be installed on Homebrew systems as follows:

    brew cask install java8
    brew install maven


### Ruby

Rake tasks and Cucumber Ruby code target the Ruby version listed in `.ruby-version`.  Something like [`rvm`][rvm] or
[`rbenv`][rbenv] is recommended to install a compatible version of Ruby.

[maven]: https://maven.apache.org/
[rbenv]: https://github.com/rbenv/rbenv
[rvm]: https://rvm.io/


## Development

### Build Automation via Rake

Some things are just plain hard to automate in Maven, especially considering that there are Cucumber tests in Ruby that
test JavaSpec externally.

This is where [Rake][rake] comes in.  It offers a single place to organize and run tasks for building, testing, and
releasing sources without having to resort to less-than-intuitive Maven configurations or one-off scripts.
Rake tasks (which can be listed with `rake -T`) simply delegate to Maven, where Java sources are concerned.  The Rake
tasks attempt to be as orthogonal as possible, meaning: 

- Each task does one, logical thing.
- Tasks can be run in any combination or sequence.

So if you want to run Cucumber Ruby tests against freshly-compiled Java code, you run `rake java:compile cucumber`.
If your Java code is already compiled and you just want to run the Cucumber Ruby tests again, it's `rake cucumber`.

[rake]: https://github.com/ruby/rake


### Testing

There are a number of different testing tools that are used for testing, and tests are written in a
[variety of scopes][fowler-test-pyramid], or levels of abstraction.

- Run `rake java:junit` to run JUnit tests on Java sources, using the familiar `mvn test` workflow.
  These offer immediate feedback on JavaSpec classes, from inside the same process.
- Run `rake java:test` to run all those tests, plus [Cucumber-jvm][cucumber-jvm] tests in `info.javaspecfeature`.
  This runs acceptance tests for high-level - but less immediate - feedback on JavaSpec as a whole.
  The test code still runs in the same process as the production code being tested, so this leaves some possible gaps
  for things like runtime dependencies and class-loading.
- Run `rake cucumber` or `rake cucumber:focus` to run (focused) Cucumber Ruby tests.
  These tests focus on JavaSpec's behavior when run as an external process, so it covers things like exit codes and
  console output.  Note that the `cucumber-docker` tasks can be used to run build a Docker image to run these tests,
  if an extra layer of isolation is desired.
  

Note that - while it is possible to write the last category of tests in Java - the author's experiences have been:
 
- It's rather laborious to launch, monitor, and scrape output from external processes in Java, when it's so easy in Ruby.
- Managing two independent scopes of testing in Maven is also rather laborious and unintuitive.  It is possible to say
  what you want to test by using different test plugins (Failsafe and Surefire), plugin configurations, and Maven
  profiles, but it's practically impossible for multiple people to maintain this effectively.

One final note about the way tests are organized: it's not an explicit goal to have three separate sets of tests
that roughly correspond to the levels of the Testing Pyramid.  It just sort of turned out that way, by covering gaps
the author encountered along the way, using whatever tools seemed practical at the time.

[cucumber-jvm]: https://docs.cucumber.io/installation/java/
[fowler-test-pyramid]: https://martinfowler.com/bliki/TestPyramid.html


### Continuous Integration (CI)

Continuous Integration happens on [Travis][travis-javaspec], using the [generic image][travis-generic] that has a JDK,
Maven, and Ruby installed.  Maven dependencies are installed before the main `script` stage, just like in the
[Java image][travis-java].

Use `rake travis:lint` to run a linter on any changes to `.travis.yml`.


[travis-generic]: https://docs.travis-ci.com/user/languages/minimal-and-generic/#generic
[travis-java]: https://docs.travis-ci.com/user/languages/java/#maven-dependency-management
[travis-javaspec]: https://travis-ci.org/kkrull/javaspec


## Releasing

Rake also has tasks to automate the process of building and uploading a release.  This was as semi-automated process at
best, even in its first incarnation as Bash scripts, and it would benefit from further refinement.  Old notes guiding
the process are in [the outdated release document](release.md).

It relies upon tools such as [GPG][gpg] which will need to be installed, configured, and documented here.

It also attempts to deploy JavaSpec to [Sonatype OSS][sonatype], which may not even exist or offer free accounts
anymore, as far as the author knows.


[gpg]: https://gpgtools.org/
[sonatype]: https://www.sonatype.com/


### Artifacts

Speaking of artifacts: _which JARs get released?_

That's a great question.  JavaSpec is currently under development to add an additional, Mocha-like syntax
(while retaining the MSpec-inspired syntax from the 1.x series) as well as an external test runner.
This will likely result in more artifacts, that still need some further iteration before we can solve the problem of
independent use without creating the problem of a mess of poorly-conceived JARs.


## Design Principles

A few thoughts collected along the way, as new features are completed:

1. **Specs should be descriptive**: I was hesitant to require a description on all `Spec` implementations.  I decided
to make it a requirement after realizing -- _the whole point of this library is to make it easier to describe things_.
1. **Specs should be organized**: It is currently allowed to declare `it` anywhere, without saying what you are
describing.  I don't think that makes much sense; developers should say what they are writing tests for.  With that
in mind, it makes more sense for the eventual root suite that collects all the `describe` blocks to only allow child
suites, not specs.


I will leave you now, Adventurer, with these words of advice:

> Go forth and solve a problem, without creating any new ones to take its place.

May you be successful in your pursuit.
