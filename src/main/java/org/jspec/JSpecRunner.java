package org.jspec;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    if (!readExamples().findAny().isPresent()) {
      errors.add(new ContextClassMissingExamples());
    }
  }

  @Override
  public Description getDescription() {
    Description context = Description.createSuiteDescription(getContextClass());
    readExamples().map(Example::getDescription).forEach(context::addChild);
    return context;
  }

  @Override
  protected List<Example> getChildren() {
//    System.out.println("getChildren");
    return readExamples().collect(Collectors.toList());
  }

  @Override
  protected Description describeChild(Example child) {
//    System.out.println("describeChild");
    throw new UnsupportedOperationException();
  }

  @Override
  protected void runChild(Example child, RunNotifier notifier) {
//    System.out.println("runChild");
    Description description = child.getDescription();
    
    notifier.fireTestStarted(description);
    notifier.fireTestFinished(description);
  }
  
  Stream<Example> readExamples() {
    List<Field> behaviors = ReflectionUtil.fieldsOfType(It.class, getContextClass());
    return behaviors.stream().map(x -> new Example(x));
  }
  
  Class<?> getContextClass() {
    return getTestClass().getJavaClass();
  }
  
  static class ContextClassMissingExamples extends InitializationError {
    public ContextClassMissingExamples() {
      super("A JSpec class must declare 1 or more It fields");
    }
  }
}