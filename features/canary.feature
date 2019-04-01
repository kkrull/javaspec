@focus
Feature: Cucumber Canary
  As a developer who is working on JavaSpec
  In order to know my tests are working
  I want to run some low level tests and verify their results


  # https://relishapp.com/cucumber/aruba/v/0-11-0/docs/command/all-output-of-commands-which-were-executed#detect-exact-one-line-output-with-ansi-output
  @keep-ansi-escape-sequences
  Scenario: Cucumber should be able to tell when a process has no colorized output
    Given a file named "hello_world_plain.rb" with:
      """
      puts "Hello World!"
      """
    When I successfully run `ruby ./hello_world_plain.rb`
    Then the output should contain "Hello World!"
    And the output should not contain:
    """
    \e[
    """


  @keep-ansi-escape-sequences
  Scenario: Cucumber should be able to detect escape sequences for colorized output
    Given a file named "hello_world_color.rb" with:
    """
    puts "\e[34mColorized Text\e[0m"
    """
    When I successfully run `ruby ./hello_world_color.rb`
    Then the output should contain "Colorized Text"
    And the output should contain:
    """
    \e[
    """
