Feature: Basic structure of a JavaSpec test class
  As a developer who writes tests
  In order to write those tests quickly and maintain them more easily in the future
  I want an expressive syntax that describes the desired behavior of the production code in a concise way, without
  sacrificing readability
  
  Scenario: Each It field is a test
    Given I have a JavaSpec test with 1 or more It fields that are assigned to no-argument lambdas
    When I run the tests
    Then the test runner should run one test for every It field
  
  Scenario: An unassigned It field is a pending test
    Given I have a JavaSpec test with a blank It field
    When I run the test
    Then the test runner should ignore the test

  Scenario: Relative order of execution for test fixture lambdas
    Given I have a JavaSpec test with test fixture lambdas
    When I run the test
    Then the test runner should run the test within the context of the test fixture
    And the test runner should run the Establish lambda first, to arrange conditions necessary for the test
    And the test runner should run the Because lambda second, to invoke the behavior in question
    And the test runner should run the It lambda third, to make one logical assertion about the outcome
    And the test runner should run the Cleanup lambda fourth, to put everything back the way it was before