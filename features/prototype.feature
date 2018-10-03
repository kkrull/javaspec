Feature: Console Runner
  In order to tell which code is working and which code is broken
  As a Java developer
  I want to run tests on the command line

  Scenario: Run a single spec class
    Given I have a Java class that contains 1 or more specs
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should indicate whether each spec passed or failed
    And The runner should indicate whether all specs passed, or any failed
