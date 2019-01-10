package info.javaspec.console;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.MockSpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import static info.javaspec.lang.lambda.FunctionalDsl.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

/** Steps about spec declaration forms */
public class SpecSyntaxSteps {
  private Class<?> specDeclarationClass;
  private String thatIntendedBehavior;
  private RunAssertion specLambdasRan;
  private Suite suite;

  @Given("^I have a spec declaration that calls `it` with a lambda and a description of intended behavior$")
  public void iHaveASpecDeclarationCallingIt() throws Exception {
    specDeclarationClass = OneSpies.class;
    thatIntendedBehavior = "does a thing";
    specLambdasRan = () -> OneSpies.assertRanNumTimes(1);
  }

  @When("^I load the specs from that declaration$")
  public void iLoadTheSpecsFromThatDeclaration() throws Exception {
    InstanceSpecFinder finder = new InstanceSpecFinder();
    suite = finder.findSpecs(specDeclarationClass);
  }

  @Then("^a spec should exist with the given description$")
  public void aSpecShouldExistWithThatDescription() throws Exception {
    assertThat(suite.intendedBehaviors(), containsInAnyOrder(thatIntendedBehavior));
  }

  @When("^I run that spec$")
  public void iRunThatSpec() throws Exception {
    suite.runSpecs(new MockSpecReporter());
  }

  @Then("^that lambda should be run$")
  public void thatLambdaShouldBeRun() throws Exception {
    specLambdasRan.verify();
  }

  public static final class OneSpies {
    private static int numTimesRan = 0;

    public static void assertRanNumTimes(int expected) {
      assertThat(numTimesRan, equalTo(expected));
    }

    public OneSpies() {
      it("does a thing", () -> numTimesRan++);
    }
  }

  @FunctionalInterface
  interface RunAssertion {
    void verify();
  }
}
