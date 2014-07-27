package org.jspec;

import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public final class JSpecRunner extends ParentRunner<Object> {

  protected JSpecRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  protected List<Object> getChildren() {
    return new LinkedList<Object>();
  }

  @Override
  protected Description describeChild(Object child) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void runChild(Object child, RunNotifier notifier) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Description getDescription() {
    String context = getTestClass().getName().replace('_',  ' ');
    return Description.createSuiteDescription(context, getTestClass().getAnnotations());
  }
}