package info.javaspec.console;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
  format = { "pretty", "html:build/reports/cucumber", "rerun:build/cucumber-jvm-rerun.txt" },
  strict = true,
  tags = { "~@wip" })
public final class FeatureTest { }
