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
    * sags to the ground, when a coyote with an anvil is standing on it: PASS
    * recoils when the coyote drops the anvil: PASS

    [Testing complete] Passed: 2, Failed: 0, Total: 2
    """


  Scenario: 2 or more subjects should be separated by a newline
    Given I have a JavaSpec runner for the console
    And I have specs that describe 2 or more subjects
    When I run those specs with a plain text reporter
    Then the runner's output should be
    """
    Anvil
    * levitates mid-air, to avoid falling on a passing road runner: PASS

    Tightrope
    * sags to the ground, when a coyote with an anvil is standing on it: PASS
    * recoils when the coyote drops the anvil: PASS

    [Testing complete] Passed: 3, Failed: 0, Total: 3
    """


  Scenario: Nested context should be indented
    Given I have a JavaSpec runner for the console
    And I have specs that describe context-specific behavior
    When I run those specs with a plain text reporter
    Then the runner's output should be
    """
    Spring-operated boxing glove
      when the spring expands
      * pushes the rock holding it backwards: PASS

      when the spring contracts again
      * punches any nearby coyote in the face: PASS

    [Testing complete] Passed: 2, Failed: 0, Total: 2
    """


  @focus
  @wip
  Scenario: Failing specs should report failure details
    Given I have a JavaSpec runner for the console
    And I have specs where 1 or more of them fail
    When I run those specs with a plain text reporter
    Then the runner's de-tabbed output should be
    """
    Anvil (Coyote perspective)
    * falls onto a passing road runner: FAIL [1]

    Specs failed:
    [1] java.lang.AssertionError: The anvil was supposed to fall, but it is levitating in mid-air
      at info.javaspec.example.rb.CoyoteAnvilSpecs.lambda$null$0(CoyoteAnvilSpecs.java:10)
      at info.javaspec.lang.lambda.DescriptiveSpec.run(DescriptiveSpec.java:24)
      at info.javaspec.lang.lambda.SequentialCollection.lambda$runSpecs$0(SequentialCollection.java:50)
      at java.lang.Iterable.forEach(Iterable.java:75)
      at info.javaspec.lang.lambda.SequentialCollection.runSpecs(SequentialCollection.java:50)
      at info.javaspec.lang.lambda.RootCollection.lambda$runSpecs$0(RootCollection.java:37)
      at java.util.ArrayList.forEach(ArrayList.java:1257)
      at info.javaspec.lang.lambda.RootCollection.runSpecs(RootCollection.java:37)
      at info.javaspec.lang.lambda.RunSpecsCommand.run(RunSpecsCommand.java:26)
      at info.javaspec.console.Main.runCommand(Main.java:28)
      at info.javaspec.console.Main.main(Main.java:20)
      at info.javaspec.console.Main.main(Main.java:10)

    [Testing complete] Passed: 0, Failed: 1, Total: 1
    """
