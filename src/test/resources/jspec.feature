Feature: JSpec runner

  Scenario: Run JSpec tests with a JSpecRunner
    Given I have a class with JSpec tests in it
    When I run the tests with a JSpec runner
    Then the test runner should run all the tests in the class

  Scenario: Run JSpec tests with a JUnit4 runner
    Given I have a class with JSpec tests in it that is marked to run with a JSpec runner
    When I run the tests with a JUnit runner
    Then the test runner should run all the tests in the marked class
    
  Scenario: Relative order of execution for test fixture functions
    Given I have JSpec test with test fixture functions
    When I run the test
    Then the test runner should run the test within the context of the test fixture
    And the test runner should run the Establish function first, to arrange conditions necessary for the test
    And the test runner should run the Because function second, to invoke the behavior in question
    And the test runner should run the It function third, to make one logical assertion about the outcome
#    And the test runner should run the Cleanup function fourth, to put everything back the way it was before