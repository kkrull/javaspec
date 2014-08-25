package org.javaspec.runner;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public final class JavaSpecRunner extends ParentRunner<NewExample> {
  private final ExampleGateway exampleGateway;
  
  public JavaSpecRunner(Class<?> contextClass) throws InitializationError {
    this(new ClassExampleGateway(contextClass));
  }
  
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
    return describe(exampleGateway.getRootContext());
  }

  @Override
  protected Description describeChild(NewExample child) {
    return describeExample(child.getContextName(), child.describeBehavior());
  }

  private Description describe(Context context) {
    Description suite = Description.createSuiteDescription(context.name);
    context.getSubContexts().stream().map(this::describe).forEach(suite::addChild);
    exampleGateway.getExampleNames(context).stream().map(x -> describeExample(context.name, x))
      .forEach(suite::addChild);
    
    return suite;
  };
  
  private Description describeExample(String contextName, String exampleName) {
    return Description.createTestDescription(contextName, exampleName);
  }
  
  @Override
  protected List<NewExample> getChildren() {
    return exampleGateway.getExamples().collect(toList());
  }
  
  @Override
  protected void runChild(NewExample child, RunNotifier notifier) {
    Description description = describeChild(child);
    if(child.isSkipped())
      notifier.fireTestIgnored(description);
    else
      runExample(child, notifier, description);
  }

  private void runExample(NewExample child, RunNotifier notifier, Description description) {
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