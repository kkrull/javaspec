# Developing JavaSpec

This guide covers common development tasks.  Make sure you have [set up your
development environment](../dev-environment/readme.md) first.

## Start with Gradle

Start by getting familiar with how [`gradle`](./gradle-usage.md) is used here.

## Common Development Tasks

This project uses Gradle to standardize the process of doing lots of common
things, like formatting source code.  [Github Actions][github-actions] handle
CI/CD (Continuous Integration/Continuous Delivery).

[github-actions]: https://docs.github.com/en/actions

- [Add license and copyright notices](./legal-notices.md)
- [Assemble JARs](./assemble-jars.md)
- [Format source files](./format-sources.md)
- [Test Java code with the JUnit Platform](./test-java-code.md)

## Debugging Tools

There are some tools that can help to understand how JavaSpec is running under
the JUnit Platform, if you get stuck.


### Log a `DiscoveryRequest` with `javaspec-engine-discovery-request-listener`

Add this dependency if you need to have a look at the `DiscoveryRequest` that
`JavaSpecEngine` receives, when it is looking for specs:

```groovy
//build.gradle
dependencies {
  testRuntimeOnly project(':javaspec-engine-discovery-request-listener')
}
```

You also need to add some more configuration so that Gradle and/or JUnit
Platform allow the `TestEngine` to write to the console.

```groovy
//build.gradle
test {
  //Show all output: the specs themselves *and* the TestEngine
  onOutput { _descriptor, TestOutputEvent engineEvent ->
    logger.lifecycle(engineEvent.message.trim())
  }
}
```


### Log test execution events with `jupiter-test-execution-listener`

You can add this dependency at runtime to get some more information about what
is happening when `JavaSpecEngine` is running specs.

```groovy
//build.gradle
dependencies {
  testRuntimeOnly project(':jupiter-test-execution-listener')
}
```

