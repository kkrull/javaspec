package org.javaspec.runner;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public final class JavaSpecRunner extends ParentRunner<Example> {
  private final ExampleGateway gateway;
  
  public JavaSpecRunner(Class<?> contextClass) throws InitializationError {
    this(new ClassExampleGateway(contextClass));
  }
  
  JavaSpecRunner(ExampleGateway gateway) throws InitializationError {
    super(null); //Bypass JUnit's requirements for a context class; throw our own errors instead
    this.gateway = gateway;
    
    List<Throwable> initializationErrors = findInitializationErrors(gateway);
    if(!initializationErrors.isEmpty()) {
      throw new InitializationError(initializationErrors);
    }
  }
  
  private List<Throwable> findInitializationErrors(ExampleGateway gateway) {
    List<Throwable> initializationErrors = gateway.findInitializationErrors();
    if(!gateway.hasExamples()) {
      initializationErrors.add(new NoExamplesException(gateway.getRootContextName()));
    }
    
    return initializationErrors;
  }
  
  /* Describing tests */
  
  @Override
  public Description getDescription() {
    return describeSuite(gateway.getRootContext());
  }

  @Override
  protected Description describeChild(Example child) {
    return describeTest(child.getContextName(), child.getName());
  }

  private Description describeSuite(Context context) {
    Description suite = Description.createSuiteDescription(context.name);
    gateway.getSubContexts(context).stream().map(this::describeSuite).forEach(suite::addChild);
    context.getExampleNames().stream().map(x -> describeTest(context.name, x)).forEach(suite::addChild);
    return suite;
  };
  
  private Description describeTest(String contextName, String exampleName) {
    return Description.createTestDescription(contextName, exampleName);
  }
  
  /* Running tests */
  
  @Override
  protected List<Example> getChildren() {
    return gateway.getExamples().collect(toList());
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
  
  public static class NoExamplesException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public NoExamplesException(String contextName) {
      super(String.format("Test context '%s' must contain at least 1 example in an It field", contextName));
    }
  }
}