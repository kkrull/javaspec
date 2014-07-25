Feature: JSpec runner

  Scenario: Run JSpec tests
    Given I have a class with JSpec tests in it
    When I run the tests with a JUnit runner
    Then the test runner should run all the tests in the class