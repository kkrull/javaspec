package org.jspec.runner;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jspec.dsl.It;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

/** A JUnit4 runner for JSpec test classes containing 1 or more It fields */
public final class JSpecRunner extends ParentRunner<Example> {

  public JSpecRunner(Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  protected void collectInitializationErrors(List<Throwable> errors) {
    super.collectInitializationErrors(errors);
    Stream.of(findInvalidConstructorError(), findNoExampleError())
      .filter(x -> x != null)
      .forEach(errors::add);
  }
  
  private InitializationError findInvalidConstructorError() {
    Constructor<?> constructor;
    try {
      constructor = getTestClass().getOnlyConstructor();
    } catch (AssertionError _ex) {
      return new InvalidConstructorError();
    }
    
    return constructor.getParameterCount() == 0 ? null : new InvalidConstructorError();
  }

  private InitializationError findNoExampleError() {
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
    return child.getDescription();
  }

  @Override
  protected void runChild(Example child, RunNotifier notifier) {
    Description description = child.getDescription();
    notifier.fireTestStarted(description);
    try {
      child.run(getContextInstance());
    } catch (Throwable t) { //Gotta catch 'em all, especially AssertionErrors (I told you he was tricksy)
      notifier.fireTestFailure(new Failure(description, t));
    } finally {
      notifier.fireTestFinished(description);
    }
  }
  
  private Stream<Example> readExamples() {
    return ReflectionUtil.fieldsOfType(It.class, getContextClass()).map(Example::new);
  }
  
  private Object getContextInstance() throws ReflectiveOperationException {
    return getTestClass().getOnlyConstructor().newInstance();
  }
  
  private Class<?> getContextClass() {
    return getTestClass().getJavaClass();
  }
  
  public static class NoExamplesError extends InitializationError {
    private static final long serialVersionUID = -4731749974843465573L;

    NoExamplesError() {
      super("A JSpec class must declare 1 or more It fields");
    }
  }
  
  public static class InvalidConstructorError extends InitializationError {
    private static final long serialVersionUID = 7403176586548921108L;

    InvalidConstructorError() {
      super("A JSpec test must have a public, no-arg constructor and no others");
    }
  }
}