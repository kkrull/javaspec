@keep-ansi-escape-sequences
@log_commands
Feature: Run specs with plaintext reporter
  As a developer who is working on some code that is covered by specs
  In order to know which specs are running, passing, and failing as well as how they are failing
  I want to run specs on the command line and see a text report of specs and their results

  As a developer who is working on JavaSpec
  In order to demonstrate how JavaSpec runs specs and reports results on the console
  I want to run JavaSpec from code where it is easy to scrape console output and to define how it should be presented

  ## Run specifics: How does a plaintext reporter present information to the developer?

  Scenario: Text output should still be legible, when running specs in a terminal that doesn't grok ANSI color codes
  Note: Although plugins exist to parse and/or strip color codes from the output, the escape sequences used to change
  color and move around on the terminal are often output as plain text on Jenkins.

    Given I have a JavaSpec runner for the console
    And I have 2 or more spec collections with a variety of results
    When I run those specs with a plain text reporter
    Then the runner's output should not contain any ANSI escape sequences


  Scenario: Specs should be shown as a bulleted list under the subject
    Given I have a JavaSpec runner for the console
    And I have specs that describe a single subject
    When I run those specs with a plain text reporter
    Then the runner's output should be
    """
    Tightrope
    - supports a coyote holding an anvil: FAIL
    - recoils when the coyote drops the anvil: PASS

    [Testing complete] Passed: 1, Failed: 1, Total: 2
    """


  Scenario: 2 or more subjects should be separated by a newline
    Given I have a JavaSpec runner for the console
    And I have specs that describe 2 or more subjects
    When I run those specs with a plain text reporter
    Then the runner's output should be
    """
    Anvil
    - falls on a passing road runner: FAIL

    Tightrope
    - supports a coyote holding an anvil: FAIL
    - recoils when the coyote drops the anvil: PASS

    [Testing complete] Passed: 1, Failed: 2, Total: 3
    """
