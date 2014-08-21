Feature: Nested JavaSpec context classes for hierarchical testing
  As a developer with some unit of production code that has 2 or more distinct behaviors
  In order to describe each of those behaviors without repeating myself
  I want to organize my tests in a hierarchy of test contexts, so that common fixture and functionality can be shared
  among the tests that need them
  
  Scenario: Tests can be defined at any level of an inner class structure
    Note: An inner class is a non-static, nested classes that can access members defined in its enclosing class.
    Static nested classes wouldn't be able to access members in enclosing classes.
    
    Given I have a top-level class marked to run with a JavaSpec runner
    And that class contains 1 or more inner classes
    When I run the tests
    Then the test runner should run tests for each It field in the top-level class
    And the test runner should run tests for each It field in an inner class
  
  @wip
  Scenario: Context scope for a test
    Given I have a top-level class marked to run with a JavaSpec runner
    And that class and its inner classes define fixture lambdas
    When I run the tests
    Then each test runs within the context defined by the fixture lambdas in the test's own class and in each enclosing class
    And pre-test fixture lambdas run top-down, starting with the top-level class
    And post-test fixture lambdas run bottom-up, starting with the class defining the test
  
  @wip
  Scenario: Relative order of Establish and Because lambdas in nested contexts
    In other words, the test runner takes exactly one trip down the context class hierarchy to run pre-test lambdas
    instead of taking one trip for the Establish lambdas followed by a second trip for the Because lambdas.
    
    Given I have a top-level class marked to run with a JavaSpec runner
    And that class and its inner classes each define Establish and Because fixture lambdas
    When I run the tests
    Then an Establish lambda runs before a Because lambda, if both are in the same class
    And both of these run before any Establish or Because lambdas in any nested classes