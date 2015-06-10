package info.javaspecfeature;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(//features = {"target"},
  format = { "pretty", "html:target/cucumber", "rerun:target/rerun.txt" },
  monochrome = true,
  tags = { "~@wip" })
public final class FeatureTest { }