Feature: JSpec runner

  Scenario: Run JSpec tests with a JSpecRunner
    Given I have a class with JSpec tests in it
    When I run the tests with a JSpec runner
    Then the test runner should run all the tests in the class

  Scenario: Run JSpec tests with a JUnit4 runner
    Given I have a class with JSpec tests in it that is marked to run with a JSpec runner
    When I run the tests with a JUnit runner
    Then the test runner should run all the tests in the marked class