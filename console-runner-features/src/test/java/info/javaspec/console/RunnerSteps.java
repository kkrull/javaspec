package info.javaspec.console;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.Suite;
import info.javaspec.console.Mock.MockExitHandler;
import info.javaspec.console.Mock.MockSpec;
import info.javaspec.console.Mock.MockSpecReporter;
import info.javaspec.console.Prototype.StaticSuite;

/** Steps observing what happens in a Runner, from within the same process */
public class RunnerSteps {
  private SuiteRunner runner;
  private MockSpecReporter mockReporter;
  private MockExitHandler system;

  private Suite suite;
  private MockSpec passingSpec;
  private MockSpec failingSpec;

  @Given("^I have a JavaSpec runner for the console$")
  public void iHaveAConsoleRunner() throws Exception {
    this.system = new MockExitHandler();
    this.mockReporter = new MockSpecReporter();
    this.runner = (suite) -> Runner.main(suite, this.mockReporter, this.system);
  }

  @Given("^I have a Java class that defines a suite of lambda specs$")
  public void iHaveAJavaClassWithASuiteOfLambdaSpecs() throws Exception {
    this.passingSpec = MockSpec.runPasses();
    this.failingSpec = MockSpec.runThrows(new AssertionError("bang!"));
    this.suite = new StaticSuite(this.passingSpec, this.failingSpec);
  }

  @Given("^I have a Java class that defines a suite of passing lambda specs$")
  public void iHaveAJavaClassThatDefinesASuiteOfPassingLambdaSpecs() throws Exception {
    this.passingSpec = MockSpec.runPasses();
    this.suite = new StaticSuite(this.passingSpec);
  }

  @Given("^I have a Java class that defines a suite of 1 or more failing lambda specs$")
  public void iHaveASuiteWithFailingSpecs() throws Exception {
    this.failingSpec = MockSpec.runThrows(new AssertionError("bang!"));
    this.suite = new StaticSuite(this.failingSpec);
  }

  @When("^I run the specs in that class$")
  public void whenRunningSpecs() throws Exception {
    this.runner.run(this.suite);
  }

  @Then("^The runner should run the specs defined in that class$")
  public void thenRunnerShouldRunSpecs() throws Exception {
    this.mockReporter.specStartingShouldHaveReceived(this.failingSpec, this.passingSpec);
    this.failingSpec.runShouldHaveBeenCalled();
    this.passingSpec.runShouldHaveBeenCalled();
  }

  @Then("^The runner should indicate which specs passed and failed$")
  public void theRunnerShouldIndicateWhichSpecsPassedAndFailed() throws Exception {
    this.mockReporter.specPassedShouldHaveReceived(this.passingSpec);
    this.mockReporter.specFailedShouldHaveReceived(this.failingSpec);
  }

  @Then("^The runner should indicate that 1 or more specs have failed$")
  public void theRunnerShouldIndicateFailingStatus() throws Exception {
    this.system.exitShouldHaveReceived(1);
  }

  @Then("^The runner should indicate that all specs passed$")
  public void theRunnerShouldIndicateThatAllSpecsPassed() throws Exception {
    this.system.exitShouldHaveReceived(0);
  }

  @FunctionalInterface
  interface SuiteRunner {
    void run(Suite suite);
  }
}
