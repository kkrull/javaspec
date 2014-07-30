package org.jspec;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspec.dsl.It;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
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
    Stream.of(findInvalidConstructorError(), findNoExampleError())
      .filter(x -> x != null)
      .forEach(errors::add);
  }
  
  InitializationError findInvalidConstructorError() {
    try {
      getTestClass().getOnlyConstructor();
      return null;
    } catch (AssertionError _ex) {
      return new InvalidConstructorError();
    }
  }

  InitializationError findNoExampleError() {
    return readExamples().findAny().isPresent() ? null : new NoExamplesError();
  }

  @Override
  public Description getDescription() {
    Description context = Description.createSuiteDescription(getContextClass());
    readExamples().map(Example::getDescription).forEach(context::addChild);
    return context;
  }

  @Override
  protected List<Example> getChildren() {
    return readExamples().collect(Collectors.toList());
  }

  @Override
  protected Description describeChild(Example child) {
    System.out.println("describeChild");
    throw new UnsupportedOperationException();
  }

  @Override
  protected void runChild(Example child, RunNotifier notifier) {
    Description description = child.getDescription();
    notifier.fireTestStarted(description);
    try {
      child.run(getContextInstance());
    } catch (Throwable t) { //Gotta catch 'em all (especially AssertionErrors; you know they're tricksy)
      notifier.fireTestFailure(new Failure(description, t));
    } finally {
      notifier.fireTestFinished(description);
    }
  }
  
  Stream<Example> readExamples() {
    return ReflectionUtil.fieldsOfType(It.class, getContextClass()).map(Example::new);
  }
  
  Object getContextInstance() throws ReflectiveOperationException {
    return getTestClass().getOnlyConstructor().newInstance();
  }
  
  Class<?> getContextClass() {
    return getTestClass().getJavaClass();
  }
  
  static class NoExamplesError extends InitializationError {
    private static final long serialVersionUID = -4731749974843465573L;

    public NoExamplesError() {
      super("A JSpec class must declare 1 or more It fields");
    }
  }
  
  static class InvalidConstructorError extends InitializationError {
    private static final long serialVersionUID = 7403176586548921108L;

    public InvalidConstructorError() {
      super("A JSpec test must have a public, no-arg constructor and no others");
    }
  }
}