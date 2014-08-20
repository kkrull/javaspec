Feature: JavaSpec runner
  As a developer who writes tests
  In order to write those tests quickly and more easily maintain them
  I want to be able to express those tests using a more concise syntax than can be done with test methods

  Scenario: Run JavaSpec tests with a JavaSpecRunner
    Given I have a class with JavaSpec tests in it
    When I run the tests with a JavaSpec runner
    Then the test runner should run all the tests in the class

  Scenario: Run JavaSpec tests with a JUnit4 runner
    Given I have a class with JavaSpec tests in it that is marked to run with a JavaSpec runner
    When I run the tests with a JUnit runner
    Then the test runner should run all the tests in the marked class

  Scenario: Blank It field is a pending test that gets ignored
    Given I have JavaSpec test with a blank It field
    When I run the test
    Then the test runner should ignore the test

  Scenario: Relative order of execution for test fixture functions
    Given I have JavaSpec test with test fixture functions
    When I run the test
    Then the test runner should run the test within the context of the test fixture
    And the test runner should run the Establish function first, to arrange conditions necessary for the test
    And the test runner should run the Because function second, to invoke the behavior in question
    And the test runner should run the It function third, to make one logical assertion about the outcome
    And the test runner should run the Cleanup function fourth, to put everything back the way it was before