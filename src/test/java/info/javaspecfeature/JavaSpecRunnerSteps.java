package info.javaspecfeature;

import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import info.javaspec.runner.Runners;
import info.javaspec.testutil.RunListenerSpy.Event;
import info.javaspecproto.ContextClasses;
import info.javaspecproto.OuterContext;
import info.javaspecproto.OuterContextWithSetup;
import info.javaspecproto.RunWithJavaSpecRunner;

import java.util.LinkedList;
import java.util.List;

import org.junit.runner.JUnitCore;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public final class JavaSpecRunnerSteps {
  private final List<Object> events = synchronizedList(new LinkedList<Object> ()); //In case JUnit uses threads per test
  private Class<?> testClass;
  
  @Before
  public void deploySpies() {
    ContextClasses.FullFixture.setEventListener(events::add);
    ContextClasses.OneIt.setEventListener(events::add);
    ContextClasses.TwoIt.setEventListener(events::add);
    RunWithJavaSpecRunner.setEventListener(events::add);
  }
  
  @After
  public void recallSpies() {
    ContextClasses.FullFixture.setEventListener(null);
    ContextClasses.OneIt.setEventListener(null);
    ContextClasses.TwoIt.setEventListener(null);
    RunWithJavaSpecRunner.setEventListener(null);
  }
  
  /* Given */

  @Given("^I have a class with JavaSpec tests in it$")
  public void i_have_a_class_with_JavaSpec_tests_in_it() throws Throwable {
    this.testClass = ContextClasses.OneIt.class;
  }
  
  @Given("^I have a class with JavaSpec tests in it that is marked to run with a JavaSpec runner$")
  public void i_have_a_class_with_JavaSpec_tests_in_it_that_is_marked_to_run_with_a_JavaSpec_runner() throws Throwable {
    this.testClass = RunWithJavaSpecRunner.class;
  }
  
  @Given("^I have a JavaSpec test with test fixture lambdas$")
  public void i_have_a_JavaSpec_test_with_test_fixture_lambdas() throws Throwable {
    this.testClass = ContextClasses.FullFixture.class;
  }

  @Given("^I have a JavaSpec test with 1 or more It fields that are assigned to no-argument lambdas$")
  public void i_have_a_JavaSpec_test_with_It_fields_that_are_assigned_to_no_argument_lambdas() throws Throwable {
    this.testClass = ContextClasses.TwoIt.class;
  }

  @Given("^I have a JavaSpec test with a blank It field$")
  public void i_have_a_JavaSpec_test_with_a_blank_It_field() throws Throwable {
    this.testClass = ContextClasses.PendingIt.class;
  }
  
  @Given("^I have a top-level class marked to run with a JavaSpec runner$")
  public void i_have_a_top_level_class_marked_to_run_with_a_JavaSpec_runner() { /* Followed by a more specific step */ }

  @Given("^that class contains 1 or more inner classes$")
  public void that_class_contains_or_more_inner_classes() throws Throwable {
    this.testClass = OuterContext.class;    
  }
  
  @Given("^that class and its inner classes define fixture lambdas$")
  public void that_class_and_its_inner_classes_define_fixture_lambdas() throws Throwable {
    this.testClass = OuterContext.class;
  }
  
  @Given("^that class and its inner classes each define Establish and Because fixture lambdas$")
  public void that_class_and_its_inner_classes_each_define_Establish_and_Because_fixture_lambdas() throws Throwable {
    this.testClass = OuterContextWithSetup.class;
  }
  
  /* When */
  
  @When("^I run the tests?$")
  public void i_run_the_test() throws Throwable {
    Runners.runAll(Runners.of(testClass), events::add);
  }
  
  @When("^I run the tests with a JavaSpec runner$")
  public void i_run_the_tests_with_a_JavaSpec_runner() throws Throwable {
    Runners.runAll(Runners.of(testClass), events::add);
  }

  @When("^I run the tests with a JUnit runner$")
  public void i_run_the_tests_with_a_JUnit_runner() throws Throwable {
    JUnitCore.runClasses(testClass);
  }
  
  /* Then */
  
  @Then("^the test runner should run all the tests in the class$")
  public void the_test_runner_should_run_all_the_tests_in_the_class() throws Throwable {
    assertThat(describeEvents(), executedLambdas(), hasItems("ContextClasses.OneIt::only_test"));
  }

  @Then("^the test runner should run all the tests in the marked class$")
  public void the_test_runner_should_run_all_the_tests_in_the_marked_class() throws Throwable {
    assertThat(describeEvents(), executedLambdas(), hasItems("RunWithJavaSpecRunner::only_test"));
  }
  
  @Then("^the test runner should run one test for every It field$")
  public void the_test_runner_should_run_one_test_for_every_It_field() throws Throwable {
    assertThat(describeEvents(), executedLambdas(), contains("TwoIt::first_test", "TwoIt::second_test"));
  }
  
  @Then("^the test runner should run the test within the context of the test fixture$")
  public void the_test_runner_should_run_the_test_within_the_context_of_the_test_fixture() throws Throwable {
    assertThat(describeEvents(), executedLambdas(), hasSize(5));
  }

  @Then("^the test runner should run the Establish lambda first.*$")
  public void the_test_runner_should_run_the_Establish_lambda_first() throws Throwable {
    assertThat(describeEvents(), executedLambdas().get(1), equalTo("ContextClasses.FullFixture::arrange"));
  }

  @Then("^the test runner should run the Because lambda second.*$")
  public void the_test_runner_should_run_the_Because_lambda_second() throws Throwable {
    assertThat(describeEvents(), executedLambdas().get(2), equalTo("ContextClasses.FullFixture::act"));
  }

  @Then("^the test runner should run the It lambda third.*$")
  public void the_test_runner_should_run_the_It_lambda_third() throws Throwable {
    assertThat(describeEvents(), executedLambdas().get(3), equalTo("ContextClasses.FullFixture::assert"));
  }

  @Then("^the test runner should run the Cleanup lambda fourth.*$")
  public void the_test_runner_should_run_the_Cleanup_lambda_fourth() throws Throwable {
    assertThat(describeEvents(), executedLambdas().get(4), equalTo("ContextClasses.FullFixture::cleans"));
  }

  @Then("^the test runner should ignore the test$")
  public void the_test_runner_should_ignore_the_test() throws Throwable {
    assertThat(describeEvents(), notificationEventNames(), contains("testIgnored"));
  }
  
  @Then("^the test runner should run tests for each It field in the top-level class$")
  public void the_test_runner_should_run_tests_for_each_It_field_in_the_top_level_class() throws Throwable {
    assertTestRan(OuterContext.class, "asserts");
  }

  @Then("^the test runner should run tests for each It field in an inner class$")
  public void the_test_runner_should_run_tests_for_each_It_field_in_an_inner_class() throws Throwable {
    assertTestRan(OuterContext.InnerContext.class, "asserts");
  }
  
  @Then("^each test runs within the context defined by the fixture lambdas in the test's own class and in each enclosing class$")
  public void each_test_runs_within_the_context_defined_by_the_fixture_lambdas_in_the_tests_own_class_and_in_each_enclosing_class() throws Throwable {
    assertThat(describeEvents(), notificationEventNames(), not(hasItem("testFailure")));
  }

  @Then("^pre-test fixture lambdas run top-down, starting with the top-level class$")
  public void pre_test_fixture_lambdas_run_top_down_starting_with_the_top_level_class() throws Throwable {
    assertTestPassed(OuterContext.class, "asserts");
    assertTestPassed(OuterContext.InnerContext.class, "asserts");
  }

  @Then("^post-test fixture lambdas run bottom-up, starting with the class defining the test$")
  public void post_test_fixture_lambdas_run_bottom_up_starting_with_the_class_defining_the_test() throws Throwable {
    //If the test passed, then this has already been verified
  }
  
  @Then("^an Establish lambda runs before a Because lambda, if both are in the same class$")
  public void an_Establish_lambda_runs_before_a_Because_lambda_if_both_are_in_the_same_class() throws Throwable {
    assertTestPassed(OuterContextWithSetup.class, "asserts");
    assertTestPassed(OuterContextWithSetup.InnerContextWithSetup.class, "asserts");
  }

  @Then("^both of these run before any Establish or Because lambdas in any nested classes$")
  public void both_of_these_run_before_any_Establish_or_Because_lambdas_in_any_nested_classes() throws Throwable {
    //If the test passed, then this has already been verified
  }
  
  /* Helpers */
  
  private void assertTestPassed(Class<?> context, String itFieldName) {
    assertThat(describeEvents(), notificationsForTest(context, itFieldName, "testFinished"), hasSize(1));
    assertThat(describeEvents(), notificationsForTest(context, itFieldName, "testFailed"), hasSize(0));
  }

  private void assertTestRan(Class<?> context, String itFieldName) {
    assertThat(describeEvents(), notificationsForTest(context, itFieldName, "testStarted"), hasSize(1));
    assertThat(describeEvents(), notificationsForTest(context, itFieldName, "testFinished"), hasSize(1));
  }

  private List<Event> notificationsForTest(Class<?> context, String itFieldName, String eventName) {
    List<Event> match = notificationEvents().stream()
      .filter(x -> eventName.equals(x.name))
      .filter(x -> context.getSimpleName().equals(x.describedClassName()))
      .filter(x -> itFieldName.equals(x.describedMethodName()))
      .collect(toList());
    return match;
  }
  
  private String describeEvents() {
    return String.format("\nActual: %s", events);
  }
  
  private List<String> executedLambdas() {
    return events.stream()
      .filter(x -> x instanceof String)
      .map(x -> (String)x)
      .collect(toList());
  }
  
  private List<String> notificationEventNames() {
    return notificationEvents().stream().map(Event::getName).collect(toList());
  }
  
  private List<Event> notificationEvents() {
    return events.stream()
      .filter(x -> x instanceof Event)
      .map(x -> (Event)x)
      .collect(toList());
  }
}