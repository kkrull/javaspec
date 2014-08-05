package org.jspec.runner;

import org.junit.runners.model.InitializationError;

public class NewJSpecRunner {
  NewJSpecRunner(TestConfiguration config) throws InitializationError {
    if(config.hasInitializationErrors()) {
      throw new InitializationError(config.findInitializationErrors());
    }
  }
}