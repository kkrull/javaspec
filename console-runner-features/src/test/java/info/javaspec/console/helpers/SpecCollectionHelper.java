package info.javaspec.console.helpers;

import info.javaspec.MockSpecReporter;
import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.FunctionalDslStrategy;
import info.javaspec.lang.lambda.SpecCollectionFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpecCollectionHelper {
  private final SpecHelper specHelper;

  private SpecCollection rootCollection;
  private SpecCollection selectedCollection;

  public SpecCollectionHelper(SpecHelper specHelper) {
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
    List<Class<?>> specClasses = Collections.singletonList(this.specHelper.getDeclaringClass());
    List<String> specClassNames = specClasses.stream()
      .map(Class::getName)
      .collect(Collectors.toList());
    SpecCollectionFactory factory = new FunctionalDslStrategy(specClassNames);
    this.rootCollection = factory.declareSpecs();
  }

  public void runThatCollection() {
    runner().run(getSelectedCollection());
  }

  public SpecCollection getSelectedCollection() {
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
