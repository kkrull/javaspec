package org.jspec;

import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public final class JSpecRunner extends ParentRunner<Example> {

  protected JSpecRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  public Description getDescription() {
    String context = getTestClass().getName().replace('_', ' ');
    return Description.createSuiteDescription(context, getTestClass().getAnnotations());
  }

  @Override
  protected List<Example> getChildren() {
    LinkedList<Example> examples = new LinkedList<Example>();
    return examples;
  }

  @Override
  protected Description describeChild(Example child) {
    System.out.println("describeChild");
    throw new UnsupportedOperationException();
  }

  @Override
  protected void runChild(Example child, RunNotifier notifier) {
    System.out.println("runChild");
    throw new UnsupportedOperationException();
  }
}