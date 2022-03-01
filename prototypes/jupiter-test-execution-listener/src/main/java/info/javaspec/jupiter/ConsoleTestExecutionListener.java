package info.javaspec.jupiter;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

//Logs events to the console
//Register via: https://junit.org/junit5/docs/current/user-guide/#launcher-api-launcher-discovery-listeners-custom
public class ConsoleTestExecutionListener implements TestExecutionListener {
  public ConsoleTestExecutionListener() { /* Required as service wrapper */ }

  @Override
  public void testPlanExecutionStarted(TestPlan plan) {
    System.out.printf(
      "[ConsoleTestExecutionListener#testPlanExecutionStarted] %d tests%n",
      plan.countTestIdentifiers(x -> true));
  }

  @Override
  public void testPlanExecutionFinished(TestPlan plan) {
    System.out.printf("[ConsoleTestExecutionListener#testPlanExecutionFinished]%n");
  }

  @Override
  public void dynamicTestRegistered(TestIdentifier id) {
    System.out.printf("[ConsoleTestExecutionListener#dynamicTestRegistered] %s%n", id);
  }

  @Override
  public void executionSkipped(TestIdentifier id, String reason) {
    System.out.printf("[ConsoleTestExecutionListener#executionSkipped] %s: %s%n", id, reason);
  }

  @Override
  public void executionStarted(TestIdentifier id) {
    System.out.printf("[ConsoleTestExecutionListener#executionStarted] %s%n", id);
  }

  @Override
  public void executionFinished(TestIdentifier id, TestExecutionResult result) {
    System.out.printf("[ConsoleTestExecutionListener#executionFinished] %s: %s%n", id, result);
  }

  @Override
  public void reportingEntryPublished(TestIdentifier id, ReportEntry entry) {
    System.out.printf("[ConsoleTestExecutionListener#reportingEntryPublished] %s: %s%n", id, entry);
  }
}
