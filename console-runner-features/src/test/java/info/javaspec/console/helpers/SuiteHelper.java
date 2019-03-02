package info.javaspec.console.helpers;

import info.javaspec.MockSpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.FunctionalDslDeclaration;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.Collections;
import java.util.List;

public class SuiteHelper {
  private final SpecHelper specHelper;

  private Suite rootSuite;
  private Suite selectedSuite;

  public SuiteHelper(SpecHelper specHelper) {
    this.specHelper = specHelper;
  }

  public Suite findChildSuiteWithDescription(String description) {
    this.selectedSuite = getRootSuite().childSuites().stream()
      .filter(x -> description.equals(x.description()))
      .findFirst()
      .orElseThrow(() -> new RuntimeException(String.format("Suite not found: %s", description)));

    return this.selectedSuite;
  }

  public void loadSpecsFromClass() {
    InstanceSpecFinder finder = new InstanceSpecFinder(strategy -> {
      FunctionalDslDeclaration.beginDeclaration();
      strategy.declareSpecs();
      return FunctionalDslDeclaration.endDeclaration();
    });
    List<Class<?>> specClasses = Collections.singletonList(this.specHelper.getDeclaringClass());
    this.rootSuite = finder.findSpecs(specClasses);
  }

  public void runThatSuite() {
    runner().run(getSelectedSuite());
  }

  public Suite getSelectedSuite() {
    if(this.selectedSuite != null)
      return this.selectedSuite;

    Suite rootSuite = getRootSuite();
    if(rootSuite != null)
      return rootSuite;

    throw new RuntimeException("No suite of specs has been defined");
  }

  private Suite getRootSuite() {
    return this.rootSuite;
  }

  private SuiteRunner runner() {
    return suite -> suite.runSpecs(new MockSpecReporter());
  }

  @FunctionalInterface
  public interface SuiteRunner {
    void run(Suite suite);
  }
}
