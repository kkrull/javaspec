package info.javaspec.console.helpers;

import info.javaspec.MockReporter;
import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.FunctionalDslFactory;
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
    ClassLoader specClassLoader = getClass().getClassLoader();
    List<Class<?>> specClasses = Collections.singletonList(this.specHelper.getDeclaringClass());
    List<String> specClassNames = specClasses.stream()
      .map(Class::getName)
      .collect(Collectors.toList());

    SpecCollectionFactory factory = new FunctionalDslFactory(specClassLoader, specClassNames);
    this.rootCollection = factory.declareSpecs();
  }

  public void runThatCollection() {
    getSelectedCollection().runSpecs(new MockReporter());
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
}
