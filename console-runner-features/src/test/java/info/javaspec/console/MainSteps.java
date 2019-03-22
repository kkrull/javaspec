package info.javaspec.console;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.MockReporter;

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
      MainStepsOneOfEach.reset();
      Main.main(this.mockReporter, this.system, "run", MainStepsOneOfEach.class.getCanonicalName());
    };

    this.declaredSpecsRan = MainStepsOneOfEach::specsShouldHaveRun;
    this.expectedResultsReported = () -> {
      this.mockReporter.specShouldHavePassed("passes");
      this.mockReporter.specShouldHaveFailed("fails");
    };
  }

  @Given("^I have a Java class that defines a suite of passing lambda specs$")
  public void iHaveAJavaClassThatDefinesPassingLambdaSpecs() throws Exception {
    this.execRunCommand = () -> Main.main(
      this.mockReporter,
      this.system,
      "run",
      MainStepsOnePasses.class.getCanonicalName()
    );
  }

  @Given("^I have a Java class that defines a suite of 1 or more failing lambda specs$")
  public void iHaveAClassWithFailingSpecs() throws Exception {
    this.execRunCommand = () -> Main.main(
      this.mockReporter,
      this.system,
      "run",
      MainStepsOneFails.class.getCanonicalName()
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
