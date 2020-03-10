@log_commands
Feature: Run command (external process)
  As a developer who is working on some code that is covered by specs
  In order to know which specs are running, passing, and failing as well as how they are failing
  I want to run specs on the command line and see a report of specs and their results

  As a developer who is working on JavaSpec
  In order to demonstrate how JavaSpec runs specs and reports results on the console
  I want to run JavaSpec from a separate process and scrape its console output


  ## Run basics: What does executing the `run` command tell the developer?

  Scenario: The run command should run specs and tell you what happened
    Given I have a JavaSpec runner for the console
    And I have a Java class with specs that pass, as well as specs that fail
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should indicate which specs passed and failed
    And The runner should describe what went wrong with each failing spec


  Scenario: The run command should describe what is being tested
    Given I have a JavaSpec runner for the console
    And I have a Java class containing specs that describe a subject
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should describe the subject being tested


  Scenario: The run command should run multiple Java classes
    Given I have a JavaSpec runner for the console
    And I have 2 or more Java classes that define lambda specs
    When I run the specs in those classes
    Then The runner should run the specs in each of those classes

  @focus
  @wip
  @java_class_runner
  Scenario: The run command should load spec classes with external dependencies
    Given I have a JavaSpec runner for the console
    And I have a Java class with specs that depend upon external classes
    When I run the specs in that class
    Then The runner should indicate that all specs passed
    And The runner should run the specs defined in that class

  ## Error conditions: How does a developer figure out what went wrong **and how to fix it**?

  Scenario: The run command should fail when it can't find a spec class
    Given I have a JavaSpec runner for the console
    And I have a Java class that defines a suite of passing lambda specs
    When I run the specs with an incorrect classpath entry for that class
    Then The runner should list which spec classes could not be loaded
    And The runner should indicate that running specs failed
