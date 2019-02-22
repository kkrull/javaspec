package info.javaspec.console;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.MockSpecReporter;
import info.javaspec.SequentialSuite;
import info.javaspec.SpecReporter;
import info.javaspec.console.helpers.SuiteHelper;
import info.javaspec.lang.lambda.MockSpec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/** Steps observing what happens in the overall process of running specs, from *within* the same process */
public class MainSteps {
  private final SuiteHelper suiteHelper;

  private MockSpecReporter mockReporter;
  private Verification declaredSpecsRan;
  private Verification expectedResultsReported;

  public MainSteps(SuiteHelper suiteHelper) {
    this.suiteHelper = suiteHelper;
    this.declaredSpecsRan = () -> { throw new UnsupportedOperationException("Verification not defined"); };
    this.expectedResultsReported = () -> { throw new UnsupportedOperationException("Verification not defined"); };
  }

  @Given("^I have a JavaSpec runner for the console$")
  public void iHaveAConsoleRunner() throws Exception {
    this.mockReporter = new MockSpecReporter();
    this.suiteHelper.setRunner(suite -> {
      Command runCommand = (SpecReporter reporter) -> {
        this.mockReporter.runStarting();
        suite.runSpecs(this.mockReporter);
        this.mockReporter.runFinished();
        return 42;
      };

      Main main = new Main(this.mockReporter, new MockExitHandler());
      main.runCommand(runCommand);
    });
  }

  @Given("^I have a Java class that defines a suite of lambda specs$")
  public void iHaveAJavaClassWithASuiteOfLambdaSpecs() throws Exception {
    //TODO KDK: Refactor to load real classes, instead of building mock instances.
    //Then change the runner to call Main#main(SpecReporter, ExitHandler, String... args)
    MockSpec passingSpec = new MockSpec.Builder()
      .withIntendedBehavior("passes")
      .thatPasses()
      .build();

    MockSpec failingSpec = new MockSpec.Builder()
      .withIntendedBehavior("fails")
      .thatFailsWith(new AssertionError("bang!"))
      .build();

    SequentialSuite suite = new SequentialSuite();
    suite.addSpec(passingSpec);
    suite.addSpec(failingSpec);
    this.suiteHelper.setRootSuite(suite);

    this.declaredSpecsRan = () -> {
      this.mockReporter.specShouldHaveBeenStarted(passingSpec);
      passingSpec.runShouldHaveBeenCalled();
      this.mockReporter.specShouldHaveBeenStarted(failingSpec);
      failingSpec.runShouldHaveBeenCalled();
    };

    this.expectedResultsReported = () -> {
      this.mockReporter.specShouldHavePassed(passingSpec);
      this.mockReporter.specShouldHaveFailed(failingSpec);
    };
  }

  @Given("^I have a Java class that defines a suite of passing lambda specs$")
  public void iHaveAJavaClassThatDefinesASuiteOfPassingLambdaSpecs() throws Exception {
    MockSpec passingSpec = new MockSpec.Builder()
      .withIntendedBehavior("passes")
      .thatPasses()
      .build();

    SequentialSuite suite = new SequentialSuite();
    suite.addSpec(passingSpec);
    this.suiteHelper.setRootSuite(suite);
  }

  @Given("^I have a Java class that defines a suite of 1 or more failing lambda specs$")
  public void iHaveASuiteWithFailingSpecs() throws Exception {
    MockSpec failingSpec = new MockSpec.Builder()
      .withIntendedBehavior("fails")
      .thatFailsWith(new AssertionError("bang!"))
      .build();

    SequentialSuite suite = new SequentialSuite();
    suite.addSpec(failingSpec);
    this.suiteHelper.setRootSuite(suite);
  }

  @When("^I run that spec$")
  public void iRunThatSpec() throws Exception {
    this.suiteHelper.runThatSuite();
  }

  @When("^I run the specs in that class$")
  public void whenRunningSpecs() throws Exception {
    this.suiteHelper.runThatSuite();
  }

  @Then("^The runner should run the specs defined in that class$")
  public void theRunnerShouldRunSpecs() throws Exception {
    this.mockReporter.runStartingShouldHaveBeenCalled();
    this.declaredSpecsRan.verify();
    this.mockReporter.runFinishedShouldHaveBeenCalled();
  }

  @Then("^The runner should run each suite of specs that are defined in that class$")
  public void theRunnerShouldRunSuites() throws Exception {
    this.mockReporter.suiteShouldHaveBeenStarted(this.suiteHelper.getSelectedSuite());
  }

  @Then("^The runner should indicate which specs passed and failed$")
  public void theRunnerShouldIndicateWhichSpecsPassedAndFailed() throws Exception {
    this.expectedResultsReported.verify();
  }

  @Then("^The runner should indicate that 1 or more specs have failed$")
  public void theReporterShouldIndicateFailingStatus() throws Exception {
    assertThat(this.mockReporter.hasFailingSpecs(), equalTo(true));
  }

  @Then("^The runner should indicate that all specs passed$")
  public void theReporterShouldIndicateThatAllSpecsPassed() throws Exception {
    assertThat(this.mockReporter.hasFailingSpecs(), equalTo(false));
  }
}
