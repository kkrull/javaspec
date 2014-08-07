package org.jspec.runner;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public class JSpecRunner extends ParentRunner<Example> {
  private final TestConfiguration config;
  
  public JSpecRunner(Class<?> contextClass) throws InitializationError {
    this(ContextTestConfiguration.forClass(contextClass));
  }
  
  JSpecRunner(TestConfiguration config) throws InitializationError {
    super(null); //Bypass JUnit's requirements for a context class; throw our own errors instead
    this.config = config;
    if(config.hasInitializationErrors()) {
      throw new InitializationError(config.findInitializationErrors());
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
    return config.getExamples();
  }
  
  @Override
  protected Description describeChild(Example child) {
    return Description.createTestDescription(config.getContextClass(), child.describeBehavior());
  }
  
  @Override
  protected void runChild(Example child, RunNotifier notifier) {
    Description description = describeChild(child);
    notifier.fireTestStarted(description);
    try {
      child.run(null);
    } catch (Exception | AssertionError e) {
      notifier.fireTestFailure(new Failure(description, e));
    } finally {
      notifier.fireTestFinished(description);
    }
  }
}