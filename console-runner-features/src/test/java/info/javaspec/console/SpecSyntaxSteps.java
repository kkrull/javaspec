package info.javaspec.console;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.Suite;
import info.javaspec.console.helpers.SpecHelper;
import info.javaspec.console.helpers.SuiteHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static info.javaspec.lang.lambda.FunctionalDsl.describe;
import static info.javaspec.lang.lambda.FunctionalDsl.it;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

/** Steps about spec declaration forms */
public class SpecSyntaxSteps {
  private final SpecHelper specHelper;
  private final SuiteHelper suiteHelper;

  private String thatDescription;
  private String thatIntendedBehavior;
  private List<String> thoseIntendedBehaviors;
  private RunAssertion specLambdasRan;

  public SpecSyntaxSteps(SpecHelper specHelper, SuiteHelper suiteHelper) {
    this.specHelper = specHelper;
    this.suiteHelper = suiteHelper;
  }

  @Given("^I have a spec declaration that calls `it` with a lambda and a description of intended behavior$")
  public void iHaveASpecDeclarationCallingIt() throws Exception {
    specHelper.setDeclaringClass(OneSpies.class);
    thatIntendedBehavior = "does a thing";
    specLambdasRan = () -> OneSpies.assertRanNumTimes(1);
  }

  @Given("^I have a spec declaration that calls `describe` with a class and a lambda containing 1 or more `it` statements$")
  public void iHaveASpecDeclarationCallingDescribe() throws Exception {
    specHelper.setDeclaringClass(DescribeTwo.class);
    thatDescription = "Illudium Q-36 Explosive Space Modulator";
    thoseIntendedBehaviors = new ArrayList<>(Arrays.asList("discombobulates", "explodes"));
  }

  @When("^I load the specs from that declaration$")
  public void iLoadTheSpecsFromThatDeclaration() throws Exception {
    suiteHelper.loadSpecsFromClass();
  }

  @When("^I run that spec$")
  public void iRunThatSpec() throws Exception {
    suiteHelper.runThatSuite();
  }

  @Then("^a spec should exist with the given description$")
  public void aSpecShouldExistWithThatDescription() throws Exception {
    assertThat(suiteHelper.thatSuite().intendedBehaviors(), containsInAnyOrder(thatIntendedBehavior));
  }

  @Then("^that lambda should be run$")
  public void thatLambdaShouldBeRun() throws Exception {
    specLambdasRan.verify();
  }

  @Then("^there should be a suite with that description$")
  public void thereShouldBeASuiteWithThatDescription() throws Exception {
    Suite thatSuite = suiteHelper.findChildSuiteWithDescription(thatDescription);
    assertThat(thatSuite.description(), equalTo(thatDescription));
  }

  @Then("^that suite should contain a spec for each `it` statement within it$")
  public void thatSuiteShouldHaveSpecs() throws Exception {
    assertThat(suiteHelper.thatSuite().intendedBehaviors(), equalTo(thoseIntendedBehaviors));
  }

  public static final class DescribeTwo {{
    describe("Illudium Q-36 Explosive Space Modulator", () -> {
      it("discombobulates", () -> {});
      it("explodes", () -> {});
    });
  }}

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
