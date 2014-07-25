package org.jspec;

import static org.junit.Assert.*;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public final class JSpecRunnerSteps {
	Class<?> testClass;
	Result result;
	
	@Given("^I have a class with JSpec tests in it$")
	public void i_have_a_class_with_JSpec_tests_in_it() throws Throwable {
		this.testClass = JSpecTests.class;
	}

	@When("^I run the tests with a JUnit runner$")
	public void i_run_the_tests_with_a_JUnit_runner() throws Throwable {
		this.result = JUnitCore.runClasses(this.testClass);
	}

	@Then("^the test runner should run all the tests in the class$")
	public void the_test_runner_should_run_all_the_tests_in_the_class() throws Throwable {
		//Can't simply count tests, because the cucumber tests count too
		for(Failure f : this.result.getFailures()) {
			Throwable ex = f.getException();
			System.out.printf("Looking at a test that failed with %s: %s\n", ex.getClass().getName(), ex);
			if(JSpecTests.TestRanException.class.equals(ex.getClass())) {
				return;
			}
		}
	
		fail("JSpec test was not run, or did not fail as expected");
	}
}