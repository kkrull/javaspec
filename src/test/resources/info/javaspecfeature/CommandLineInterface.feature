Feature: Command Line Interface
  As a developer using JavaSpec out in the wild
  In order to verify my deployment process, submit a bug report, or submit a feature request
  I want to find out what version is being used in this environment

  Scenario: Usage statement on no arguments
    When I run JavaSpec without any arguments
    Then the command line interface should print a usage statement to the console that describes how it may be used
    And the command line interface should exit with status 0

  Scenario: Usage statement on unrecognized arguments
    When I run JavaSpec with unsupported arguments
    Then the command line interface should print a usage statement to the console that describes how it may be used
    And the command line interface should exit with status 1

  Scenario: Ask for help
    When I ask the JavaSpec runner for help
    Then the command line interface should print a usage statement to the console that describes how it may be used
    And the command line interface should exit with status 0

  Scenario: Current version
    When I ask the JavaSpec runner for its version
    Then the command line interface should print its version number to the console
    And the command line interface should exit with status 0