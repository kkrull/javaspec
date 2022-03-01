package info.javaspec.engine;

import info.javaspec.api.SpecClass;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JavaSpecEngine implements TestEngine {
  @Override
  public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId engineId) {
    System.out.printf("[JavaSpecEngine#discover]%n");
    printDiscoveryRequest(discoveryRequest);

    EngineDescriptor engineDescriptor = new EngineDescriptor(engineId, "JavaSpec");
    discoveryRequest.getSelectorsByType(ClassSelector.class).stream()
      .map(ClassSelector::getJavaClass)
      .filter(selectedClass -> SpecClass.class.isAssignableFrom(selectedClass))
      .map(selectedClass -> (Class<SpecClass>) selectedClass)
      .map(specClass -> makeDeclaringInstance(specClass))
      .forEach(declaringInstance -> {
        JavaSpecForJupiter javaspec = JavaSpecForJupiter.forSpecClass(engineId, declaringInstance.getClass());
        declaringInstance.declareSpecs(javaspec);
        javaspec.addDescriptorsTo(engineDescriptor);
      });

    return engineDescriptor;
  }

  //TODO KDK: Extract to ServiceLoader.  Put the interface in javaspec-api.
  //Try this https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html?is-external=true#load(java.lang.Class)
  private void printDiscoveryRequest(EngineDiscoveryRequest discoveryRequest) {
    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] ClasspathResourceSelector%n");
    discoveryRequest.getSelectorsByType(ClasspathResourceSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getClasspathResourceName()));

    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] ClasspathRootSelector%n");
    discoveryRequest.getSelectorsByType(ClasspathRootSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getClasspathRoot()));

    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] ClassSelector%n");
    discoveryRequest.getSelectorsByType(ClassSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getClassName()));

    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] DirectorySelector%n");
    discoveryRequest.getSelectorsByType(DirectorySelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getRawPath()));

    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] FileSelector%n");
    discoveryRequest.getSelectorsByType(FileSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getRawPath()));

    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] MethodSelector%n");
    discoveryRequest.getSelectorsByType(MethodSelector.class)
      .forEach(x -> System.out.printf("- %s#%s%n", x.getClassName(), x.getMethodName()));

    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] PackageSelector%n");
    discoveryRequest.getSelectorsByType(PackageSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getPackageName()));

    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] UniqueIdSelector%n");
    discoveryRequest.getSelectorsByType(UniqueIdSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getUniqueId()));

    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] UriSelector%n");
    discoveryRequest.getSelectorsByType(UriSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getUri().getRawPath()));

    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] ClassNameFilter%n");
    discoveryRequest.getFiltersByType(ClassNameFilter.class)
      .forEach(x -> System.out.printf("- %s%n", x));

    System.out.println();
    System.out.printf("[JavaSpecEngine#discover] PackageNameFilter%n");
    discoveryRequest.getFiltersByType(PackageNameFilter.class)
      .forEach(x -> System.out.printf("- %s%n", x));
  }

  private SpecClass makeDeclaringInstance(Class<SpecClass> specClass) {
    Constructor<SpecClass> constructor = null;
    try {
      constructor = specClass.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Failed to access no-arg SpecClass constructor", e);
    }

    try {
      return constructor.newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Failed to instantiate SpecClass", e);
    }
  }

  @Override
  public void execute(ExecutionRequest request) {
    execute(
      request.getRootTestDescriptor(),
      request.getEngineExecutionListener()
    );
  }

  private void execute(TestDescriptor descriptor, EngineExecutionListener listener) {
    listener.executionStarted(descriptor);

    if (descriptor.isTest()) {
      JupiterSpec spec = (JupiterSpec) descriptor;
      spec.run();
    } else if (descriptor.isContainer()) {
      for (TestDescriptor child : descriptor.getChildren()) {
        try {
          execute(child, listener);
        } catch (AssertionError|Exception e) {
          listener.executionFinished(child, TestExecutionResult.failed(e));
        }
      }
    } else {
      //Throwing exceptions from here didn't seem to cause any warnings, error messages, or failures.
      System.out.println(String.format("*** Unsupported descriptor type: %s ***", descriptor.getClass().getName()));
    }

    listener.executionFinished(descriptor, TestExecutionResult.successful());
  }

  @Override
  public String getId() {
    return "javaspec-engine";
  }
}
