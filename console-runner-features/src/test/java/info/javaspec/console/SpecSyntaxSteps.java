package info.javaspec.console;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;
import org.hamcrest.Matchers;

import static info.javaspec.lang.lambda.FunctionalDsl.it;
import static org.hamcrest.MatcherAssert.assertThat;

/** Steps about spec declaration forms */
public class SpecSyntaxSteps {
  private Class<?> specClass;
  private String thatDescription;
  private Suite suite;

  @Given("^I have a spec declaration that calls `it` with a description and a lambda$")
  public void iHaveASpecDeclarationCallingIt() throws Exception {
    specClass = OneSpies.class;
    thatDescription = "does a thing";
  }

  @When("^I load the specs from that declaration$")
  public void iLoadTheSpecsFromThatDeclaration() throws Exception {
    InstanceSpecFinder finder = new InstanceSpecFinder();
    suite = finder.findSpecs(specClass);
  }

  @Then("^a spec should exist with the given description$")
  public void aSpecShouldExistWithThatDescription() throws Exception {
    assertThat(suite.specDescriptions(), Matchers.containsInAnyOrder(thatDescription));
  }

  @When("^I run that spec$")
  public void iRunThatSpec() throws Exception {
    throw new PendingException();
  }

  @Then("^that lambda should be run$")
  public void thatLambdaShouldBeRun() throws Exception {
    throw new PendingException();
  }

  public static final class OneSpies {
    {
      it("does a thing", () -> {});
    }
  }
}
