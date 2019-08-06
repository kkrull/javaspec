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
    And I have a Java class with specs that pass, as well as specs that fail
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should indicate which specs passed and failed
    And The runner should describe what went wrong with each failing spec


  Scenario: The CLI should describe what is being tested
    Given I have a JavaSpec runner for the console
    And I have a Java class containing specs that describe a subject
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should describe the subject being tested


  Scenario: The CLI should run multiple Java classes
    Given I have a JavaSpec runner for the console
    And I have 2 or more Java classes that define lambda specs
    When I run the specs in those classes
    Then The runner should run the specs in each of those classes


  ## Error conditions: How does a developer figure out what went wrong **and how to fix it**?

  Scenario: The CLI should fail when it can't find a spec class
    Given I have a JavaSpec runner for the console
    And I have a Java class that defines a suite of passing lambda specs
    When I run the specs with an incorrect classpath entry for that class
    Then The runner should list which spec classes could not be loaded
    And The runner should indicate that running specs failed
