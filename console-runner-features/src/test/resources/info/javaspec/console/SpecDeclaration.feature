Feature: Spec Declaration
  As a developer who is reading and writing specifications for 1 or more logical units of subject code
  In order to define and understand the intended behavior of the subject code
  I want to declare specifications that describe intended behavior, that provide necessary context for this
  behavior, and that distinguish one logical unit of code's behavior from that of all other units.


  Scenario: `it` should associate a behavior-verifying procedure with a description of the intended behavior
    Given I have a spec declaration that calls `it` with a lambda and a description of intended behavior
    When I load the specs from that declaration
    Then a spec should exist with the given description

    When I run that spec declaration
    Then that lambda should be run


  Scenario: `describe` should group 1 or more specifications that describe the same Java class
    Given I have a spec declaration that calls `describe` with a class and a lambda containing 1 or more `it` statements
    When I load the specs from that declaration
    Then there should be a suite with that description
    And that suite should contain a spec for each `it` statement within it

    When I run that suite
    Then those lambdas should be run
