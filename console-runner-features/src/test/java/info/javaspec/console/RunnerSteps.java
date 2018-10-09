package info.javaspec.console;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.console.Fake.MockSpec;
import info.javaspec.console.Fake.MockSpecReporter;

/** Steps observing what happens in a Runner, from within the same process */
public class RunnerSteps {
  private Fake.SpecRunner runner;
  private Fake.Suite suite;
  private MockSpec mockSpec;
  private MockSpecReporter mockReporter;

  @Given("^I have a JavaSpec runner for the console$")
  public void iHaveAConsoleRunner() throws Exception {
    this.mockReporter = new MockSpecReporter();
    this.runner = new Fake.SpecRunner(this.mockReporter);
  }

  @Given("^I have a Java class that defines a suite of lambda specs$")
  public void iHaveAJavaClassWithASuiteOfLambdaSpecs() throws Exception {
    this.mockSpec = new MockSpec();
    this.suite = new Fake.Suite(this.mockSpec);
  }

  @When("^I run the specs in that class$")
  public void whenRunningSpecs() throws Exception {
    this.runner.run(this.suite);
  }

  @Then("^The runner should run the specs defined in that class$")
  public void thenRunnerShouldRunSpecs() throws Exception {
    this.mockReporter.specStartingShouldHaveReceived(this.mockSpec);
    this.mockSpec.runShouldHaveBeenCalled();
  }

  @Then("^The runner should indicate which specs passed and failed$")
  public void theRunnerShouldIndicateWhichSpecsPassedAndFailed() throws Exception {
    this.mockReporter.specPassedShouldHaveReceived(this.mockSpec);
  }
}
