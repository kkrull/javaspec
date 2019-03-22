@log_commands
Feature: JavaSpec CLI (external process)
  As a developer who is working on some code that is covered by specs
  In order to know which specs are running, passing, and failing as well as how they are failing
  I want to run specs on the command line and see a text report of specs and their results

  As a developer who is working on JavaSpec
  In order to have confidence that the whole system is wired up correctly
  I want to run JavaSpec as its own process and observe its behavior from a separate test process


  Scenario: The CLI should offer to help when it's run without any arguments
    Given I have a JavaSpec runner for the console
    When I run the runner without any arguments
    Then the runner's exit status should be 0
    And the runner's output should be
    """
    Usage: javaspec <command> [<arguments>]

    Commands:
      help
        show this help

      run <spec class name> [spec class name...]
        run specs in Java classes
    """


  Scenario: The CLI should run specs and tell you what happened
    Given I have a JavaSpec runner for the console
    And I have a Java class that defines a suite of lambda specs
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should indicate which specs passed and failed
    And the console output should not be so gross


  Scenario: The CLI should describe what is being tested
    Given I have a JavaSpec runner for the console
    And I have a Java class that defines a suite of lambda specs describing a subject
    When I run the specs in that class
    Then The runner should run the specs defined in that class
    And The runner should describe what is being tested


  Scenario: The CLI should report all passing specs with its exit code
    Given I have a JavaSpec runner for the console
    And I have a Java class that defines a suite of passing lambda specs
    When I run the specs in that class
    Then The runner should indicate that all specs passed


  Scenario: The CLI should report failing specs with its exit code
    Given I have a JavaSpec runner for the console
    And I have a Java class that defines a suite of 1 or more failing lambda specs
    When I run the specs in that class
    Then The runner should indicate that 1 or more specs have failed


  Scenario: The CLI should run multiple Java classes
    Given I have a JavaSpec runner for the console
    And I have 1 or more Java classes that defines lambda specs
    When I run the specs in those classes
    Then The runner should run the specs in each of those classes
