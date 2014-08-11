import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
  format = { "pretty", "html:target/cucumber", "rerun:target/rerun.txt" }, 
  monochrome = true,
  tags = {"~@wip"})
public final class FeatureTest {}