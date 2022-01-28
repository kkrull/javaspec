package info.javaspec.launcher;

import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

//Configures JUnit Jupiter to run a known spec with the JavaSpec engine
public class LauncherMain {
  //Run with ./gradlew javaspec-engine:run
  public static void main(String[] args) throws Exception {
    LauncherConfig launcherConfig = LauncherConfig.builder()
      .enableTestEngineAutoRegistration(false)
      .enableTestExecutionListenerAutoRegistration(false)
      .addTestEngines(loadTestEngine("info.javaspec.engine.JavaSpecEngine"))
      .addLauncherDiscoveryListeners(launcherDiscoveryListener())
      .addTestExecutionListeners(testExecutionListener())
      .build();

    try(LauncherSession session = LauncherFactory.openSession(launcherConfig)) {
      Launcher launcher = session.getLauncher();
      LauncherDiscoveryRequest discoveryRequest = discoverRequestForTestClass("info.javaspec.client.GreeterSpecs");
      TestPlan plan = launcher.discover(discoveryRequest);
      dfsTestIds(plan, plan.getRoots()).forEach(x ->
        System.out.printf("TestPlan %s%n", x.getUniqueId()));

      launcher.execute(plan);
    }

    assertSpecsRan("info.javaspec.client.GreeterSpecs");
  }

  private static void assertSpecsRan(String specClassName)
    throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    Class<?> specClass = Class.forName(specClassName);
    Method assertionMethod = specClass.getMethod("assertRanOnce");
    System.out.printf("Verifying tests ran with: %s::%s%n", specClass.getName(), assertionMethod.getName());
    assertionMethod.invoke(null);
  }

  private static LauncherDiscoveryRequest discoverRequestForTestClass(String specClassName) {
    return LauncherDiscoveryRequestBuilder.request()
      .selectors(selectClass(specClassName))
      .filters(includeClassNamePatterns(".*Specs"))
      .build();
  }

  private static List<TestIdentifier> dfsTestIds(TestPlan plan, Set<TestIdentifier> parentIds) {
    List<TestIdentifier> ids = new LinkedList<>();
    for(TestIdentifier parentId : parentIds) {
      ids.add(parentId);
      List<TestIdentifier> descendants = dfsTestIds(plan, plan.getChildren(parentId));
      ids.addAll(descendants);
    }

    return ids;
  }

  private static LauncherDiscoveryListener launcherDiscoveryListener() {
    return new LauncherDiscoveryListener() {
      @Override
      public void launcherDiscoveryStarted(LauncherDiscoveryRequest request) {
        System.out.println("[LauncherDiscoveryListener#launcherDiscoveryStarted]");
      }

      @Override
      public void engineDiscoveryStarted(UniqueId engineId) {
        System.out.println(String.format("[LauncherDiscoveryListener#engineDiscoveryStarted] %s", engineId));
      }

      @Override
      public void engineDiscoveryFinished(UniqueId engineId, EngineDiscoveryResult result) {
        System.out.println(String.format("[LauncherDiscoveryListener#engineDiscoveryFinished] %s: %s", engineId, result.getStatus()));
      }

      @Override
      public void launcherDiscoveryFinished(LauncherDiscoveryRequest request) {
        System.out.println("[LauncherDiscoveryListener#launcherDiscoveryFinished]");
      }
    };
  }

  private static TestEngine loadTestEngine(String engineClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Class<TestEngine> engineClass= (Class<TestEngine>) Class.forName(engineClassName);
    TestEngine javaSpecEngine = engineClass.getConstructor().newInstance();
    return javaSpecEngine;
  }

  private static TestExecutionListener testExecutionListener() {
    return new TestExecutionListener() {
      @Override
      public void testPlanExecutionStarted(TestPlan testPlan) {
        System.out.println("[TestExecutionListener#testPlanExecutionStarted]");
      }

      @Override
      public void executionStarted(TestIdentifier testId) {
        System.out.printf("[TestExecutionListener#executionStarted] %s%n", testId.getUniqueId());
      }

      @Override
      public void executionFinished(TestIdentifier testId, TestExecutionResult testExecutionResult) {
        System.out.printf("[TestExecutionListener#executionFinished] %s%n", testId.getUniqueId());
      }

      @Override
      public void testPlanExecutionFinished(TestPlan testPlan) {
        System.out.println("[TestExecutionListener#testPlanExecutionFinished]");
      }
    };
  }
}
