Feature: Main
  As a developer who is working on some code that is covered by specs
  In order to know which specs are running, passing, and failing as well as how they are failing
  I want to run specs on the command line and see a text report of specs and their results

  As a developer who is working on JavaSpec
  In order to observe side effects such as which specs have been run
  I want to run some acceptance tests from the same JVM process as JavaSpec


  Scenario: JavaSpec should run specs and tell you what happened
    Given I have a JavaSpec class runner
    And I have a Java class that defines a suite of lambda specs
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should indicate which specs passed and failed


  Scenario: JavaSpec should report failing specs with its exit code
    Given I have a JavaSpec class runner
    And I have a Java class that defines a suite of 1 or more failing lambda specs
    When I run the specs in that class
    Then The runner should indicate that 1 or more specs have failed


  Scenario: JavaSpec should report all passing specs with its exit code
    Given I have a JavaSpec class runner
    And I have a Java class that defines a suite of passing lambda specs
    When I run the specs in that class
    Then The runner should indicate that all specs passed
