package org.jspec.runner;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

public class NewJSpecRunner extends ParentRunner<Example> {
  
  public NewJSpecRunner(Class<?> contextClass) throws InitializationError {
    super(null);
  }
  
  NewJSpecRunner(TestConfiguration config) throws InitializationError {
    super(null); //TODO KDK: Is any ParentRunner functionality missing if this stays null?
    if(config.hasInitializationErrors()) {
      throw new InitializationError(config.findInitializationErrors());
    }
  }

  @Override
  protected List<Example> getChildren() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Description describeChild(Example child) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void runChild(Example child, RunNotifier notifier) {
    throw new UnsupportedOperationException();
  }
}