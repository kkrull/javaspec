Feature: Console Runner
  As a developer who is working on some code that is covered by specs
  In order to know which specs are running, passing, and failing as well as how they are failing
  I want to run specs on the command line and see a text report of specs and their results

  As a developer who is working on JavaSpec
  In order to observe side effects such as which specs have been run
  I want to run some acceptance tests from the same JVM process as JavaSpec

  Scenario: A ConsoleRunner should tell you what's happening
    Given I have a JavaSpec runner for the console
    And I have a Java class that defines a suite of lambda specs
    When I run the specs in that class
    Then The runner should run the specs defined in that class
#    And The runner should indicate whether each spec passed or failed
#    And The runner should indicate whether all specs passed, or any failed
