package info.javaspec.console;

import cucumber.api.PendingException;
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

  @Given("^I have a spec declaration that calls `describe` with a class and a lambda containing 1 or more `it` statements$")
  public void iHaveASpecDeclarationCallingDescribe() throws Exception {
    throw new PendingException();
  }

  @When("^I load the specs from that declaration$")
  public void iLoadTheSpecsFromThatDeclaration() throws Exception {
    InstanceSpecFinder finder = new InstanceSpecFinder();
    suite = finder.findSpecs(specDeclarationClass);
  }

  @When("^I run that spec$")
  public void iRunThatSpec() throws Exception {
    suite.runSpecs(new MockSpecReporter());
  }

  @Then("^a spec should exist with the given description$")
  public void aSpecShouldExistWithThatDescription() throws Exception {
    assertThat(suite.intendedBehaviors(), containsInAnyOrder(thatIntendedBehavior));
  }

  @Then("^that lambda should be run$")
  public void thatLambdaShouldBeRun() throws Exception {
    specLambdasRan.verify();
  }

  @Then("^there should be a suite with that description$")
  public void thereShouldBeASuiteWithThatDescription() throws Exception {
    throw new PendingException();
  }

  @Then("^that suite should contain a spec for each `it` statement within it$")
  public void thatSuiteHaveSpecs() throws Exception {
    throw new PendingException();
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
