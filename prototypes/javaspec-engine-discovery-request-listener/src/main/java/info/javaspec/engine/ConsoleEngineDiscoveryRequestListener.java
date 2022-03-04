package info.javaspec.engine;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.discovery.*;

//Prints JavaSpec's EngineDiscoveryRequest to the console
//Load via ServiceLoader: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html?is-external=true#load(java.lang.Class)
public class ConsoleEngineDiscoveryRequestListener implements EngineDiscoveryRequestListener {
  @Override
  public void onDiscover(EngineDiscoveryRequest discoveryRequest) {
    printDiscoveryRequest(discoveryRequest);
  }

  private void printDiscoveryRequest(EngineDiscoveryRequest discoveryRequest) {
    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] ClasspathResourceSelector%n");
    discoveryRequest.getSelectorsByType(ClasspathResourceSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getClasspathResourceName()));

    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] ClasspathRootSelector%n");
    discoveryRequest.getSelectorsByType(ClasspathRootSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getClasspathRoot()));

    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] ClassSelector%n");
    discoveryRequest.getSelectorsByType(ClassSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getClassName()));

    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] DirectorySelector%n");
    discoveryRequest.getSelectorsByType(DirectorySelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getRawPath()));

    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] FileSelector%n");
    discoveryRequest.getSelectorsByType(FileSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getRawPath()));

    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] MethodSelector%n");
    discoveryRequest.getSelectorsByType(MethodSelector.class)
      .forEach(x -> System.out.printf("- %s#%s%n", x.getClassName(), x.getMethodName()));

    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] PackageSelector%n");
    discoveryRequest.getSelectorsByType(PackageSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getPackageName()));

    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] UniqueIdSelector%n");
    discoveryRequest.getSelectorsByType(UniqueIdSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getUniqueId()));

    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] UriSelector%n");
    discoveryRequest.getSelectorsByType(UriSelector.class)
      .forEach(x -> System.out.printf("- %s%n", x.getUri().getRawPath()));

    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] ClassNameFilter%n");
    discoveryRequest.getFiltersByType(ClassNameFilter.class)
      .forEach(x -> System.out.printf("- %s%n", x));

    System.out.println();
    System.out.printf("[ConsoleEngineDiscoveryRequestListener] PackageNameFilter%n");
    discoveryRequest.getFiltersByType(PackageNameFilter.class)
      .forEach(x -> System.out.printf("- %s%n", x));
  }
}
