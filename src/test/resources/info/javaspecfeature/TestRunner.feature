Feature: Running tests
  As a developer who has production code and tests on that production code
  In order to find out if and where the production code is failing
  I want to be able run the tests in an environment that runs JUnit

  Scenario: Run JavaSpec tests with a JavaSpecRunner
    Given I have a class with JavaSpec tests in it
    When I run the tests with a JavaSpec runner
    Then the test runner should run all the tests in the class

  Scenario: Run JavaSpec tests with a JUnit4 runner
    Given I have a class with JavaSpec tests in it that is marked to run with a JavaSpec runner
    When I run the tests with a JUnit runner
    Then the test runner should run all the tests in the marked class