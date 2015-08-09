Feature: Running tests
  As a developer who has production code and tests on that production code
  In order to find out if and where the production code is failing
  I want to be able run the tests in an environment that runs JUnit

  Scenario: Count tests
    Given I have a class with JavaSpec tests in it
    When I count the tests in the class
    Then the test runner should return the number of tests that exist within the scope of that class

  Scenario: Describe tests
    Given I express desired behavior for JavaSpec through the use of Java classes and fields
    When I describe the tests in the class
    Then the test runner should describe expected behavior in human-readable language

  Scenario: Run JavaSpec tests with a JavaSpecRunner
    Given I have a class with JavaSpec tests in it
    When I run the tests with a JavaSpec runner
    Then the test runner should run all the tests in the class

  Scenario: Run JavaSpec tests with a JUnit4 runner
    Given I have a class with JavaSpec tests in it that is marked to run with a JavaSpec runner
    When I run the tests with a JUnit runner
    Then the test runner should run all the tests in the marked class
