package org.jspec;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.jspec.dsl.It;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public final class JSpecRunner extends ParentRunner<Example> {

  protected JSpecRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  protected void collectInitializationErrors(List<Throwable> errors) {
    super.collectInitializationErrors(errors);
    if (readBehaviorFields().isEmpty()) {
      errors.add(new InitializationError("A JSpec class must declare 1 or more It fields"));
    }
  }

  @Override
  public Description getDescription() {
    Description context = Description.createSuiteDescription(getContextClass());
    for (Field exampleField : readBehaviorFields()) {
      Example example = new Example(exampleField);
      context.addChild(example.getDescription());
    }

    return context;
  }

  @Override
  protected List<Example> getChildren() {
    System.out.println("getChildren");
    List<Example> tests = new LinkedList<Example>();
    for (Field exampleField : readBehaviorFields()) {
      tests.add(new Example(exampleField));
    }
    return tests;
  }

  @Override
  protected Description describeChild(Example child) {
    System.out.println("describeChild");
    throw new UnsupportedOperationException();
  }

  @Override
  protected void runChild(Example child, RunNotifier notifier) {
    System.out.println("runChild");
    Description description = child.getDescription();
    notifier.fireTestStarted(description);
    notifier.fireTestFinished(description);
  }

  List<Field> readBehaviorFields() {
    return ReflectionUtil.fieldsOfType(It.class, getContextClass());
  }

  Class<?> getContextClass() {
    return getTestClass().getJavaClass();
  }
}