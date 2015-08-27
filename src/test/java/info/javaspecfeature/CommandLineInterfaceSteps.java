package info.javaspecfeature;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.JavaSpec;
import info.javaspec.JavaSpec.ExitHandler;
import org.mockito.Mockito;

import java.io.PrintStream;

import static org.mockito.Mockito.mock;

public class CommandLineInterfaceSteps {
  private final PrintStream console = mock(PrintStream.class);
  private final ExitHandler exit = mock(ExitHandler.class);

  @When("^I run JavaSpec without any arguments$")
  public void i_run_JavaSpec_without_any_arguments() throws Exception {
    JavaSpec.main(console, exit);
  }

  @When("^I run JavaSpec with unsupported arguments$")
  public void i_run_JavaSpec_with_unsupported_arguments() throws Exception {
    JavaSpec.main(console, exit, "--bogus");
  }

  @When("^I ask the JavaSpec runner for help$")
  public void i_ask_the_JavaSpec_runner_for_help() throws Exception {
    JavaSpec.main(console, exit, "--help");
  }

  @When("^I ask the JavaSpec runner for its version$")
  public void i_ask_the_JavaSpec_runner_for_its_version() throws Exception {
    JavaSpec.main(console, exit, "--version");
  }

  @Then("^the command line interface should print a usage statement to the console that describes how it may be used$")
  public void the_command_line_interface_should_print_a_usage_statement_to_the_console() throws Exception {
    Mockito.verify(console).println("Usage: java info.javaspec.JavaSpec --version");
    Mockito.verify(console).println("--help: Show this help");
    Mockito.verify(console).println("--version: Show the version");
    Mockito.verifyNoMoreInteractions(console);
  }

  @Then("^the command line interface should print its version number to the console$")
  public void the_command_line_interface_should_print_its_version_number_to_the_console() throws Exception {
    Mockito.verify(console).println("0.5.1-SNAPSHOT");
    Mockito.verifyNoMoreInteractions(console);
  }

  @Then("^the command line interface should exit with status (\\d+)$")
  public void the_command_line_interface_should_exit_with_status(int status) throws Exception {
    Mockito.verify(exit).exit(status);
    Mockito.verifyNoMoreInteractions(exit);
  }
}