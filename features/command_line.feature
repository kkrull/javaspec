Feature: JavaSpec CLI (external process)
  As a developer who is working on some code that is covered by specs
  In order to know what code is working and where it is failing
  I want a command line interface that lets me interact with the specs I have written

  As a developer who is working on JavaSpec
  In order to have confidence that the whole system is wired up correctly
  I want to run JavaSpec as its own process and observe its behavior from a separate test process


  ## Basic CLI behavior: What commands are available?  How does `javaspec` work, as a process?

  
  Scenario: The CLI should offer to help when it's run without any arguments
    Given I have a JavaSpec runner for the console
    When I run the runner without any arguments
    Then the runner's output should be
    """
    Usage: javaspec <command> [<arguments>]

    ## Commands ##

    help  show a list of commands, or help on a specific command
    run   run specs in Java classes
    """
    And the runner's exit status should be 0


  Scenario: The CLI should tell you how to use the run command
    Given I have a JavaSpec runner for the console
    When I ask for help on the run command
    Then the runner's output should be
    """
    Usage:   javaspec run --reporter=plaintext <spec class> [spec class...]
    Example: javaspec run --reporter=plaintext com.acme.AnvilSpecs com.acme.SpringOperatedBoxingGloveSpecs

    ## Options ##

    --reporter=[reporter]   How you want to find out which spec is running and what their results are

      plaintext   Plain-text output without any colors or other escape sequences.
                  Useful for preventing garbled output on continuous integration servers.
    """
    And the runner's exit status should be 0


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
