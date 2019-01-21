package info.javaspec.console.helpers;

import info.javaspec.MockSpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.Optional;

public class SuiteHelper {
  private final SpecHelper specHelper;

  private SuiteRunner _runner;
  private Suite rootSuite;
  private Suite thatSuite;

  public SuiteHelper(SpecHelper specHelper) {
    this.specHelper = specHelper;
  }

  public Suite findChildSuiteWithDescription(String description) {
    thatSuite = rootSuite().childSuites().stream()
      .filter(x -> description.equals(x.description()))
      .findFirst()
      .orElseThrow(() -> new RuntimeException(String.format("Suite not found: %s", description)));

    return thatSuite;
  }

  public void loadSpecsFromClass() {
    InstanceSpecFinder finder = new InstanceSpecFinder();
    rootSuite = finder.findSpecs(this.specHelper.declaringClass());
  }

  public Suite thatSuite() {
    if(thatSuite != null)
      return thatSuite;
    else if(rootSuite() != null)
      return rootSuite();
    else
      throw new RuntimeException("No suite of specs has been defined");
  }

  private Suite rootSuite() {
    return rootSuite;
  }

  public void runThatSuite() {
    runner().run(thatSuite());
  }

  public void setRootSuite(Suite suite) {
    this.rootSuite = suite;
  }

  private SuiteRunner runner() {
    return Optional.ofNullable(_runner)
      .orElse(suite -> suite.runSpecs(new MockSpecReporter()));
  }

  public void setRunner(SuiteRunner runner) {
    this._runner = runner;
  }

  @FunctionalInterface
  public interface SuiteRunner {
    void run(Suite suite);
  }
}
