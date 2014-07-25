package org.jspec;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(monochrome = false, format = {"pretty", "html:target/cucumber", "rerun:target/rerun.txt"})
public class RunCukesTest {
}