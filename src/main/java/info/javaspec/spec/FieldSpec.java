package info.javaspec.spec;

import info.javaspec.dsl.Before;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.It;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Field;
import java.util.List;

import static java.util.stream.Collectors.toList;

final class FieldSpec extends Spec { //TODO KDK: Rename to DeclaredSpec?  Keep it as FieldSpec and use state pattern internally to switch from fields to Establish/It/etc?
  private final Description testDescription;
  private final Field assertionField;
  private final List<Field> befores;
  private final List<Field> afters;

  private RunnableSpec runnableSpec;

  FieldSpec(String id, Description testDescription, Field it, List<Field> befores, List<Field> afters) {
    super(id);
    this.testDescription = testDescription;
    this.assertionField = it;
    this.befores = befores;
    this.afters = afters;
  }

  @Override
  public Description getDescription() { return testDescription; }

  @Override
  public void addDescriptionTo(Description suite) {
    suite.addChild(testDescription);
  }

  @Override
  public boolean isIgnored() {
    return theRunnableSpec().hasUnassignedFunctions();
  }

  @Override
  public void run(RunNotifier notifier) {
    notifier.fireTestStarted(getDescription());
    RunnableSpec spec;
    try {
      spec = theRunnableSpec();
    } catch(Exception ex) {
      notifier.fireTestFailure(new Failure(getDescription(), ex));
      return;
    }

    try {
      spec.runBeforeSpec();
      spec.runSpec();
      spec.runAfterSpec();
    } catch(Exception | AssertionError ex) {
      notifier.fireTestFailure(new Failure(getDescription(), ex));
      return;
    }

    notifier.fireTestFinished(getDescription());
  }

  @Override
  public void run() throws Exception {
    RunnableSpec spec = theRunnableSpec();
    try {
      spec.runBeforeSpec();
      spec.runSpec();
    } finally {
      spec.runAfterSpec();
    }
  }

  private RunnableSpec theRunnableSpec() {
    if(runnableSpec == null) {
      SpecExecutionContext context = SpecExecutionContext.forDeclaringClass(assertionField.getDeclaringClass());
      try {
        List<Before> beforeValues = befores.stream().map(x -> (Before)context.getAssignedValue(x)).collect(toList());
        List<Cleanup> afterValues = afters.stream().map(x -> (Cleanup)context.getAssignedValue(x)).collect(toList());
        It assertion = (It)context.getAssignedValue(assertionField);
        runnableSpec = new RunnableSpec(assertion, beforeValues, afterValues);
      } catch(Throwable t) {
        throw TestSetupFailed.forClass(assertionField.getDeclaringClass(), t);
      }
    }

    return runnableSpec;
  }
}
