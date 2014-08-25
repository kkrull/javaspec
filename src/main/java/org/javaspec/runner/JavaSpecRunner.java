package org.javaspec.runner;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public final class JavaSpecRunner extends ParentRunner<Example> {
  private final ExampleGateway exampleGateway;
  
//  public JavaSpecRunner(Class<?> contextClass) throws InitializationError {
//    this(new ContextClassExampleGateway(contextClass));
//  }
  
  JavaSpecRunner(ExampleGateway exampleGateway) throws InitializationError {
    super(null); //Bypass JUnit's requirements for a context class; throw our own errors instead
    this.exampleGateway = exampleGateway;
    
    List<Throwable> initializationErrors = exampleGateway.findInitializationErrors();
    if(!initializationErrors.isEmpty()) {
      throw new InitializationError(initializationErrors);
    }
  }
  
  @Override
  public Description getDescription() {
    return describe(exampleGateway.getContextRoot());
  }

  private Description describe(Context context) {
    Description suite = Description.createSuiteDescription(context.value);
    context.getSubContexts().stream().map(this::describe).forEach(suite::addChild);
    exampleGateway.getExampleNames(context).stream()
      .map(x -> Description.createTestDescription(context.value, x))
      .forEach(suite::addChild);
    
    return suite;
  };
  
  @Override
  protected List<Example> getChildren() {
    throw new UnsupportedOperationException();
//    return exampleGateway.getExamples().collect(toList());
  }
  
  @Override
  protected Description describeChild(Example child) {
    throw new UnsupportedOperationException();
//    return Description.createTestDescription(exampleGateway.getContextClass(), child.describeBehavior());
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