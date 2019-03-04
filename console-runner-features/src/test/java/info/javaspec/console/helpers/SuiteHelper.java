package info.javaspec.console.helpers;

import info.javaspec.MockSpecReporter;
import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.FunctionalDsl;
import info.javaspec.lang.lambda.InstanceSpecFinder;

import java.util.Collections;
import java.util.List;

public class SuiteHelper {
  private final SpecHelper specHelper;

  private SpecCollection rootCollection;
  private SpecCollection selectedCollection;

  public SuiteHelper(SpecHelper specHelper) {
    this.specHelper = specHelper;
  }

  public SpecCollection findCollectionWithDescription(String description) {
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

  public SpecCollection getSelectedSuite() {
    if(this.selectedCollection != null)
      return this.selectedCollection;

    SpecCollection rootCollection = getRootCollection();
    if(rootCollection != null)
      return rootCollection;

    throw new RuntimeException("No collection has been defined");
  }

  private SpecCollection getRootCollection() {
    return this.rootCollection;
  }

  private SpecCollectionRunner runner() {
    return collection -> collection.runSpecs(new MockSpecReporter());
  }

  @FunctionalInterface
  interface SpecCollectionRunner {
    void run(SpecCollection collection);
  }
}
