package info.javaspec.console;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.MockReporter;
import info.javaspec.example.main.OneFailsSpecs;
import info.javaspec.example.main.OneOfEachSpecs;
import info.javaspec.example.main.OnePassesSpecs;

/** Steps observing what happens in the overall process of running specs, from *within* the same process. */
public class MainSteps {
  private RunsMain execRunCommand;
  private MockReporter mockReporter;
  private MockExitHandler system;

  private Verification declaredSpecsRan;
  private Verification expectedResultsReported;

  public MainSteps() {
    this.execRunCommand = () -> { throw new UnsupportedOperationException("execRunCommand not defined"); };
    this.declaredSpecsRan = () -> { throw new UnsupportedOperationException("Verification not defined"); };
    this.expectedResultsReported = () -> { throw new UnsupportedOperationException("Verification not defined"); };
  }

  @Given("^I have a JavaSpec class runner$")
  public void iHaveAClassRunner() throws Exception {
    this.mockReporter = new MockReporter();
    this.system = new MockExitHandler();
  }

  @Given("^I have a Java class that defines a suite of lambda specs$")
  public void iHaveAJavaClassWithLambdaSpecs() throws Exception {
    this.execRunCommand = () -> {
      OneOfEachSpecs.reset();
      Main.main(
        ArgumentParserFactory.forConsole(new StaticCommandFactory(), () -> this.mockReporter),
        () -> this.mockReporter,
        this.system,
        "run",
        "--reporter=plaintext",
        "--spec-classpath=.",
        OneOfEachSpecs.class.getCanonicalName()
      );
    };

    this.declaredSpecsRan = OneOfEachSpecs::specsShouldHaveRun;
    this.expectedResultsReported = () -> {
      this.mockReporter.specShouldHavePassed("passes");
      this.mockReporter.specShouldHaveFailed("fails");
    };
  }

  @Given("^I have a Java class that defines a suite of passing lambda specs$")
  public void iHaveAJavaClassThatDefinesPassingLambdaSpecs() throws Exception {
    this.execRunCommand = () -> Main.main(
      ArgumentParserFactory.forConsole(new StaticCommandFactory(), () -> this.mockReporter),
      () -> this.mockReporter,
      this.system,
      "run",
      "--reporter=plaintext",
      "--spec-classpath=.",
      OnePassesSpecs.class.getCanonicalName()
    );
  }

  @Given("^I have a Java class that defines a suite of 1 or more failing lambda specs$")
  public void iHaveAClassWithFailingSpecs() throws Exception {
    this.execRunCommand = () -> Main.main(
      ArgumentParserFactory.forConsole(new StaticCommandFactory(), () -> this.mockReporter),
      () -> this.mockReporter,
      this.system,
      "run",
      "--reporter=plaintext",
      "--spec-classpath=.",
      OneFailsSpecs.class.getCanonicalName()
    );
  }

  @When("^I run the specs in that class$")
  public void whenRunningSpecs() throws Exception {
    this.execRunCommand.run();
  }

  @Then("^The runner should run the specs defined in that class$")
  public void theRunnerShouldRunSpecs() throws Exception {
    this.declaredSpecsRan.verify();
  }

  @Then("^The runner should indicate which specs passed and failed$")
  public void theRunnerShouldIndicateWhichSpecsPassedAndFailed() throws Exception {
    this.expectedResultsReported.verify();
  }

  @Then("^The runner should indicate that 1 or more specs have failed$")
  public void theReporterShouldIndicateFailingStatus() throws Exception {
    this.system.exitShouldHaveReceived(1);
  }

  @Then("^The runner should indicate that all specs passed$")
  public void theReporterShouldIndicateThatAllSpecsPassed() throws Exception {
    this.system.exitShouldHaveReceived(0);
  }

  interface RunsMain {
    void run();
  }
}
