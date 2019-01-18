package info.javaspec.console.helpers;

import info.javaspec.Suite;
import info.javaspec.lang.lambda.InstanceSpecFinder;

public class SuiteHelper {
  private final SpecHelper specHelper;
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

  Suite rootSuite() {
    return rootSuite;
  }

  public void setRootSuite(Suite suite) {
    this.rootSuite = suite;
  }
}
