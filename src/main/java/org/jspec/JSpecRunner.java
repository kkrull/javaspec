package org.jspec;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public final class JSpecRunner extends Runner {

  final Class<?> testClass;
  
  public JSpecRunner(Class<?> testClass) {
    if (testClass == null)
      throw new IllegalArgumentException("testClass");
    
    this.testClass = testClass;
  }

  @Override
  public Description getDescription() {
    String context = testClass.getName().replace('_',  ' ');
    return Description.createSuiteDescription(context, testClass.getAnnotations());
  }

  @Override
  public void run(RunNotifier notifier) {
    throw new UnsupportedOperationException();
  }
}