# Installing JavaSpec

* Installation (you have the tools)
  * As with JUnit, add test section with useJUnitPlatform as usual.
  * As with JUnit, add dependencies on `org.junit.jupiter:junit-jupiter-api` and
    `org.junit.jupiter:junit-jupiter-engine` like you always would.
  * API artifact needed to declare specs - testCompile
  * Engine artifact needed at runtime to run them - testRuntimeOnly
  * Watch out for 1.x artifacts - that is for a whole different syntax, from an
    older version.
* Tool chain
  * JVM requirements - Java 11+?
  * License requirements - compatible with JUnit and Jupiter?
  * What version of JUnit?  JUnit 5.
