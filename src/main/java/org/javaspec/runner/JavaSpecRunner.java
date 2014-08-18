package org.javaspec.runner;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public final class JavaSpecRunner extends ParentRunner<Example> {
  private final TestConfiguration config;
  
  public JavaSpecRunner(Class<?> contextClass) throws InitializationError {
    this(new ContextClassTestConfiguration(contextClass));
  }
  
  JavaSpecRunner(TestConfiguration config) throws InitializationError {
    super(null); //Bypass JUnit's requirements for a context class; throw our own errors instead
    this.config = config;
    
    List<Throwable> initializationErrors = config.findInitializationErrors();
    if(!initializationErrors.isEmpty()) {
      throw new InitializationError(initializationErrors);
    }
  }
  
  @Override
  public Description getDescription() {
    Description context = Description.createSuiteDescription(config.getContextClass());
    getChildren().stream().map(this::describeChild).forEach(context::addChild);
    return context;
  };

  @Override
  protected List<Example> getChildren() {
    return config.getExamples().collect(toList());
  }
  
  @Override
  protected Description describeChild(Example child) {
    return Description.createTestDescription(config.getContextClass(), child.describeBehavior());
  }
  
  @Override
  protected void runChild(Example child, RunNotifier notifier) {
    Description description = describeChild(child);
    if(child.isSkipped())
      notifier.fireTestIgnored(description);
    else
      runExample(child, notifier, description);
  }

  private void runExample(Example child, RunNotifier notifier, Description description) {
    notifier.fireTestStarted(description);
    try {
      child.run();
    } catch (Exception | AssertionError e) {
      notifier.fireTestFailure(new Failure(description, e));
    } finally {
      notifier.fireTestFinished(description);
    }
  }
}