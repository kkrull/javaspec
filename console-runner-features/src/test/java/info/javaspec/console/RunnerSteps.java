package info.javaspec.console;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.hamcrest.Matchers;

import static org.junit.Assert.assertThat;

/** Steps observing what happens in a Runner, from within the same process */
public class RunnerSteps {
  private FakeSpecRunner runner;
  private FakeSuite suite;
  private MockSpec spec;

  @Given("^I have a JavaSpec runner for the console$")
  public void iHaveAConsoleRunner() throws Exception {
    this.runner = new FakeSpecRunner();
  }

  @Given("^I have a Java class that defines a suite of lambda specs$")
  public void iHaveAJavaClassWithASuiteOfLambdaSpecs() throws Exception {
    this.spec = new MockSpec();
    this.suite = new FakeSuite(this.spec);
  }

  @When("^I run the specs in that class$")
  public void whenRunningSpecs() throws Exception {
    this.runner.run(this.suite);
  }

  @Then("^The runner should run the specs defined in that class$")
  public void thenRunnerShouldRunSpecs() throws Exception {
    this.spec.runShouldHaveBeenCalled();
  }

  static final class FakeSpecRunner {
    public void run(FakeSuite suite) {
      suite.runSpecs();
    }
  }

  static final class FakeSuite {
    private final MockSpec spec;

    public FakeSuite(MockSpec spec) {
      this.spec = spec;
    }

    public void runSpecs() {
      this.spec.run();
    }
  }

  static final class MockSpec {
    private boolean runCalled;

    public MockSpec() {
      this.runCalled = false;
    }

    public void run() {
      this.runCalled = true;
    }

    public void runShouldHaveBeenCalled() {
      assertThat(this.runCalled, Matchers.equalTo(true));
    }
  }
}
