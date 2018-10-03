Feature: Command Line
  As a developer who is working on some code that is covered by specs
  In order to know which specs are running, passing, and failing as well as how they are failing
  I want to run specs on the command line and see a text report of specs and their results

  As a developer who is working on JavaSpec
  In order to have confidence that the whole system is wired up correctly
  I want to run JavaSpec as its own process and observe its behavior from a separate test process

  Scenario: Run specs in a single class
    Given I have a Java class that contains 1 or more specs
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should indicate whether each spec passed or failed
    And The runner should indicate whether all specs passed, or any failed
