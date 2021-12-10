package info.javaspec.engine;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherFactory;

public class EngineMain {
  //Run with ./gradlew javaspec-engine:run
  public static void main(String[] args) {
    LauncherConfig launcherConfig = LauncherConfig.builder()
      .enableTestEngineAutoRegistration(false)
      .enableTestExecutionListenerAutoRegistration(false)
      .addTestEngines(new JavaSpecEngine())
      .addLauncherDiscoveryListeners(launcherDiscoveryListener())
      .addTestExecutionListeners(testExecutionListener())
      .build();

    Launcher launcher = LauncherFactory.create(launcherConfig);
    launcher.execute(discoverRequestForTestClass());
  }

  private static LauncherDiscoveryRequest discoverRequestForTestClass() {
    throw new UnsupportedOperationException("Add dependency on test code.  Perhaps it's time to move this to a separate launcher module?");
//    return LauncherDiscoveryRequestBuilder.request()
//      .selectors(selectClass(GreeterSpecs.class))
//      .build();
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

  private static TestExecutionListener testExecutionListener() {
    return new TestExecutionListener() {
      @Override
      public void testPlanExecutionStarted(TestPlan testPlan) {
        System.out.println("[TestExecutionListener#testPlanExecutionStarted]");
      }

      @Override
      public void executionStarted(TestIdentifier testId) {
        System.out.println(String.format("[TestExecutionListener#executionStarted] %s (%s)", testId.getUniqueId(), testId.getDisplayName()));
      }

      @Override
      public void executionFinished(TestIdentifier testId, TestExecutionResult testExecutionResult) {
        System.out.println(String.format("[TestExecutionListener#executionFinished] %s (%s)", testId.getUniqueId(), testId.getDisplayName()));
      }

      @Override
      public void testPlanExecutionFinished(TestPlan testPlan) {
        System.out.println("[TestExecutionListener#testPlanExecutionFinished]");
      }
    };
  }
}
