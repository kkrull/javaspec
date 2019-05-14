@log_commands
Feature: Run command (external process)
  As a developer who is working on some code that is covered by specs
  In order to know which specs are running, passing, and failing as well as how they are failing
  I want to run specs on the command line and see a text report of specs and their results

  As a developer who is working on JavaSpec
  In order to demonstrate how JavaSpec runs specs and reports results on the console
  I want to run JavaSpec from code where it is easy to scrape console output and to define how it should be presented


  ## Run basics: What does executing the `run` command tell the developer?

  Scenario: The CLI should run specs and tell you what happened
    Given I have a JavaSpec runner for the console
    And I have a Java class that defines a suite of lambda specs
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should indicate which specs passed and failed


  Scenario: The CLI should describe what is being tested
    Given I have a JavaSpec runner for the console
    And I have a Java class that defines a suite of lambda specs describing a subject
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should describe what is being tested


  Scenario: The CLI should run multiple Java classes
    Given I have a JavaSpec runner for the console
    And I have 1 or more Java classes that defines lambda specs
    When I run the specs in those classes
    Then The runner should run the specs in each of those classes
