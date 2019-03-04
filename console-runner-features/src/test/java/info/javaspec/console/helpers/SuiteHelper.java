package info.javaspec.console.helpers;

import info.javaspec.MockSpecReporter;
import info.javaspec.Suite;
import info.javaspec.lang.lambda.FunctionalDsl;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.Collections;
import java.util.List;

public class SuiteHelper {
  private final SpecHelper specHelper;

  private Suite rootCollection;
  private Suite selectedCollection;

  public SuiteHelper(SpecHelper specHelper) {
    this.specHelper = specHelper;
  }

  public Suite findCollectionWithDescription(String description) {
    this.selectedCollection = getRootCollection().subCollections().stream()
      .filter(x -> description.equals(x.description()))
      .findFirst()
      .orElseThrow(() -> new RuntimeException(String.format("Collection not found: %s", description)));

    return this.selectedCollection;
  }

  public void loadSpecsFromClass() {
    InstanceSpecFinder finder = new InstanceSpecFinder(strategy -> {
      FunctionalDsl.openScope();
      strategy.declareSpecs();
      return FunctionalDsl.closeScope();
    });
    List<Class<?>> specClasses = Collections.singletonList(this.specHelper.getDeclaringClass());
    this.rootCollection = finder.findSpecs(specClasses);
  }

  public void runThatSuite() {
    runner().run(getSelectedSuite());
  }

  public Suite getSelectedSuite() {
    if(this.selectedCollection != null)
      return this.selectedCollection;

    Suite rootCollection = getRootCollection();
    if(rootCollection != null)
      return rootCollection;

    throw new RuntimeException("No collection has been defined");
  }

  private Suite getRootCollection() {
    return this.rootCollection;
  }

  private SpecCollectionRunner runner() {
    return collection -> collection.runSpecs(new MockSpecReporter());
  }

  @FunctionalInterface
  interface SpecCollectionRunner {
    void run(Suite collection);
  }
}
