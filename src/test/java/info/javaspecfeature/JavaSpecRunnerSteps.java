package info.javaspecfeature;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import info.javaspec.runner.Runners;
import info.javaspec.runner.ng.NewJavaSpecRunner;
import info.javaspec.testutil.RunListenerSpy.Event;
import info.javaspecproto.ContextClasses;
import info.javaspecproto.OuterContext;
import info.javaspecproto.OuterContextWithSetup;
import info.javaspecproto.RunWithJavaSpecRunner;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Runner;

import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.synchronizedList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public final class JavaSpecRunnerSteps {
  private final List<Object> events = synchronizedList(new LinkedList<>()); //In case JUnit uses threads per test
  private Class<?> testClass;
  private int numTests;
  private Description description;
  
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
  public void i_have_a_class_with_JavaSpec_tests_in_it() throws Exception {
    this.testClass = ContextClasses.OneIt.class;
  }
  
  @Given("^I have a class with JavaSpec tests in it that is marked to run with a JavaSpec runner$")
  public void i_have_a_class_with_JavaSpec_tests_in_it_that_is_marked_to_run_with_a_JavaSpec_runner() throws Exception {
    this.testClass = RunWithJavaSpecRunner.class;
  }
  
  @Given("^I have a JavaSpec test with test fixture lambdas$")
  public void i_have_a_JavaSpec_test_with_test_fixture_lambdas() throws Exception {
    this.testClass = ContextClasses.FullFixture.class;
  }

  @Given("^I have a JavaSpec test with 1 or more It fields that are assigned to no-argument lambdas$")
  public void i_have_a_JavaSpec_test_with_It_fields_that_are_assigned_to_no_argument_lambdas() throws Exception {
    this.testClass = ContextClasses.TwoIt.class;
  }

  @Given("^I have a JavaSpec test with a blank It field$")
  public void i_have_a_JavaSpec_test_with_a_blank_It_field() throws Exception {
    this.testClass = ContextClasses.PendingIt.class;
  }

  @Given("^I have a top-level class marked to run with a JavaSpec runner$")
  public void i_have_a_top_level_class_marked_to_run_with_a_JavaSpec_runner() { /* Followed by a more specific step */ }

  @Given("^that class contains 1 or more inner classes$")
  public void that_class_contains_or_more_inner_classes() throws Exception {
    this.testClass = OuterContext.class;    
  }
  
  @Given("^that class and its inner classes define fixture lambdas$")
  public void that_class_and_its_inner_classes_define_fixture_lambdas() throws Exception {
    this.testClass = OuterContext.class;
  }
  
  @Given("^that class and its inner classes each define Establish and Because fixture lambdas$")
  public void that_class_and_its_inner_classes_each_define_Establish_and_Because_fixture_lambdas() throws Exception {
    this.testClass = OuterContextWithSetup.class;
  }

  @Given("^I express desired behavior for JavaSpec through the use of Java classes and fields$")
  public void I_express_desired_behavior_for_JavaSpec_through_the_use_of_Java_classes_and_fields() throws Exception {
    this.testClass = ContextClasses.TwoIt.class;
  }

  /* When */

  @When("^I count the tests in the class$")
  public void I_count_the_tests() throws Exception {
    Runner runner = new NewJavaSpecRunner(testClass);
    this.numTests = runner.testCount();
  }

  @When("^I describe the tests in the class$")
  public void I_describe_the_tests() throws Exception {
    Runner runner = new NewJavaSpecRunner(testClass);
    this.description = runner.getDescription();
  }

  @When("^I run the tests?$")
  public void i_run_the_test() throws Exception {
    Runner runner = new NewJavaSpecRunner(testClass);
    Runners.runAll(runner, events::add);
  }
  
  @When("^I run the tests with a JavaSpec runner$")
  public void i_run_the_tests_with_a_JavaSpec_runner() throws Exception {
    Runner runner = new NewJavaSpecRunner(testClass);
    Runners.runAll(runner, events::add);
  }

  @When("^I run the tests with a JUnit runner$")
  public void i_run_the_tests_with_a_JUnit_runner() throws Exception {
    JUnitCore.runClasses(testClass);
  }
  
  /* Then */

  @Then("^the test runner should return the number of tests that exist within the scope of that class$")
  public void the_test_runner_should_return_the_number_of_tests() throws Exception {
    assertThat(numTests, equalTo(1));
  }

  @Then("^the test runner should run all the tests in the class$")
  public void the_test_runner_should_run_all_the_tests_in_the_class() throws Exception {
    assertThat(describeEvents(), executedLambdas(), hasItems("ContextClasses.OneIt::only_test"));
  }

  @Then("^the test runner should run all the tests in the marked class$")
  public void the_test_runner_should_run_all_the_tests_in_the_marked_class() throws Exception {
    assertThat(describeEvents(), executedLambdas(), hasItems("RunWithJavaSpecRunner::only_test"));
  }
  
  @Then("^the test runner should run one test for every It field$")
  public void the_test_runner_should_run_one_test_for_every_It_field() throws Exception {
    assertThat(describeEvents(), executedLambdas(), contains("TwoIt::first_test", "TwoIt::second_test"));
  }
  
  @Then("^the test runner should run the test within the context of the test fixture$")
  public void the_test_runner_should_run_the_test_within_the_context_of_the_test_fixture() throws Exception {
    assertThat(describeEvents(), executedLambdas(), hasSize(5));
  }

  @Then("^the test runner should run the Establish lambda first.*$")
  public void the_test_runner_should_run_the_Establish_lambda_first() throws Exception {
    assertThat(describeEvents(), executedLambdas().get(1), equalTo("ContextClasses.FullFixture::arrange"));
  }

  @Then("^the test runner should run the Because lambda second.*$")
  public void the_test_runner_should_run_the_Because_lambda_second() throws Exception {
    assertThat(describeEvents(), executedLambdas().get(2), equalTo("ContextClasses.FullFixture::act"));
  }

  @Then("^the test runner should run the It lambda third.*$")
  public void the_test_runner_should_run_the_It_lambda_third() throws Exception {
    assertThat(describeEvents(), executedLambdas().get(3), equalTo("ContextClasses.FullFixture::assert"));
  }

  @Then("^the test runner should run the Cleanup lambda fourth.*$")
  public void the_test_runner_should_run_the_Cleanup_lambda_fourth() throws Exception {
    assertThat(describeEvents(), executedLambdas().get(4), equalTo("ContextClasses.FullFixture::cleans"));
  }

  @Then("^the test runner should ignore the test$")
  public void the_test_runner_should_ignore_the_test() throws Exception {
    assertThat(describeEvents(), notificationEventNames(), contains("testIgnored"));
  }
  
  @Then("^the test runner should run tests for each It field in the top-level class$")
  public void the_test_runner_should_run_tests_for_each_It_field_in_the_top_level_class() throws Exception {
    assertTestRan(OuterContext.class, "asserts");
  }

  @Then("^the test runner should run tests for each It field in an inner class$")
  public void the_test_runner_should_run_tests_for_each_It_field_in_an_inner_class() throws Exception {
    assertTestRan(OuterContext.InnerContext.class, "asserts");
  }
  
  @Then("^each test runs within the context defined by the fixture lambdas in the test's own class and in each enclosing class$")
  public void each_test_runs_within_the_context_defined_by_the_fixture_lambdas_in_the_tests_own_class_and_in_each_enclosing_class() throws Exception {
    assertThat(describeEvents(), notificationEventNames(), not(hasItem("testFailure")));
  }

  @Then("^pre-test fixture lambdas run top-down, starting with the top-level class$")
  public void pre_test_fixture_lambdas_run_top_down_starting_with_the_top_level_class() throws Exception {
    assertTestPassed(OuterContext.class, "asserts");
    assertTestPassed(OuterContext.InnerContext.class, "asserts");
  }

  @Then("^post-test fixture lambdas run bottom-up, starting with the class defining the test$")
  public void post_test_fixture_lambdas_run_bottom_up_starting_with_the_class_defining_the_test() throws Exception {
    //If the test passed, then this has already been verified
  }
  
  @Then("^an Establish lambda runs before a Because lambda, if both are in the same class$")
  public void an_Establish_lambda_runs_before_a_Because_lambda_if_both_are_in_the_same_class() throws Exception {
    assertTestPassed(OuterContextWithSetup.class, "asserts");
    assertTestPassed(OuterContextWithSetup.InnerContextWithSetup.class, "asserts");
  }

  @Then("^both of these run before any Establish or Because lambdas in any nested classes$")
  public void both_of_these_run_before_any_Establish_or_Because_lambdas_in_any_nested_classes() throws Exception {
    //If the test passed, then this has already been verified
  }

  @Then("^the test runner should describe expected behavior in human-readable language$")
  public void the_test_runner_should_describe_expected_behavior() throws Exception {
    assertThat(description.getClassName(), equalTo("TwoIt"));
    assertThat(description.getChildren().stream().map(Description::getMethodName).collect(toList()),
      contains("first test", "second test"));
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
      .map(x -> (String) x)
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