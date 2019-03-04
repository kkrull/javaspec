package info.javaspec.console;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.SpecCollection;
import info.javaspec.console.helpers.SpecHelper;
import info.javaspec.console.helpers.SuiteHelper;
import info.javaspec.example.DescribeTwo;
import info.javaspec.example.OneSpies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

/** Steps about spec declaration syntax forms */
public class SpecDeclarationSteps {
  private final SpecHelper specHelper;
  private final SuiteHelper suiteHelper;

  private String thatDescription;
  private String thatIntendedBehavior;
  private List<String> thoseIntendedBehaviors;
  private Verification specLambdasRan;

  public SpecDeclarationSteps(SpecHelper specHelper, SuiteHelper suiteHelper) {
    this.specHelper = specHelper;
    this.suiteHelper = suiteHelper;
  }

  @Given("^I have a spec declaration that calls `it` with a lambda and a description of intended behavior$")
  public void iHaveASpecDeclarationCallingIt() throws Exception {
    specHelper.setDeclaringClass(OneSpies.class);
    thatDescription = "OneSpies";
    thatIntendedBehavior = "does a thing";
    specLambdasRan = () -> assertThat(OneSpies.numTimesRan, equalTo(1));
  }

  @Given("^I have a spec declaration that calls `describe` with a class and a lambda containing 1 or more `it` statements$")
  public void iHaveASpecDeclarationCallingDescribe() throws Exception {
    specHelper.setDeclaringClass(DescribeTwo.class);
    thatDescription = "Illudium Q-36 Explosive Space Modulator";
    thoseIntendedBehaviors = new ArrayList<>(Arrays.asList("discombobulates", "explodes"));
    specLambdasRan = () -> assertThat(DescribeTwo.descriptionsRan, equalTo(thoseIntendedBehaviors));
  }

  @When("^I load the specs from that declaration$")
  public void iLoadTheSpecsFromThatDeclaration() throws Exception {
    suiteHelper.loadSpecsFromClass();
  }

  @When("^I run that spec declaration$")
  public void iRunThatSpec() throws Exception {
    this.suiteHelper.runThatSuite();
  }

  @When("^I run that suite$")
  public void iRunThatSuite() throws Exception {
    this.suiteHelper.runThatSuite();
  }

  @Then("^a spec should exist with the given description$")
  public void aSpecShouldExistWithThatDescription() throws Exception {
    SpecCollection thatCollection = suiteHelper.findCollectionWithDescription(thatDescription);
    assertThat(thatCollection.intendedBehaviors(), containsInAnyOrder(thatIntendedBehavior));
  }

  @Then("^that lambda should be run$")
  public void thatLambdaShouldBeRun() throws Exception {
    specLambdasRan.verify();
  }

  @Then("^those lambdas should be run$")
  public void thoseLambdasShouldBeRun() throws Exception {
    specLambdasRan.verify();
  }

  @Then("^there should be a suite with that description$")
  public void thereShouldBeASuiteWithThatDescription() throws Exception {
    SpecCollection thatCollection = suiteHelper.findCollectionWithDescription(thatDescription);
    assertThat(thatCollection.description(), equalTo(thatDescription));
  }

  @Then("^that suite should contain a spec for each `it` statement within it$")
  public void thatSuiteShouldHaveSpecs() throws Exception {
    assertThat(suiteHelper.getSelectedSuite().intendedBehaviors(), equalTo(thoseIntendedBehaviors));
  }
}
