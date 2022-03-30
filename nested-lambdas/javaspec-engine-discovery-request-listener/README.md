# Discovery Listener

Outputs information about what JavaSpec was asked to find, when discovering specs.

## Usage

Edit `build.gradle` for your project to add this dependency:

```groovy
dependencies {
  testRuntimeOnly project(':javaspec-engine') //Should already be present, to run JavaSpec under Jupiter
  testRuntimeOnly project(':javaspec-engine-discovery-request-listener') //Add this, to log to the console
}
```

Then also allow output from the TestEngine to be shown on the console:

```groovy
test {
  useJUnitPlatform() //Should already be present, to run JavaSpec under Jupiter.

  //You need to add this too, to show console output from the JavaSpec engine listener
  onOutput { _descriptor, TestOutputEvent engineEvent ->
    logger.lifecycle(engineEvent.message.trim())
  }
}
```
