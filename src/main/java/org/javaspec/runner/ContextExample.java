package org.javaspec.runner;

import java.lang.reflect.Field;

final class ContextExample implements NewExample {
  private final String contextName;
  private final Field it;

  public ContextExample(String contextName, Field it) {
    this.contextName = contextName;
    this.it = it;
  }

  @Override
  public String getContextName() {
    return contextName;
  }
  
  @Override
  public String getName() {
    return it.getName();
  }

  @Override
  public boolean isSkipped() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void run() throws Exception {
    throw new UnsupportedOperationException();
  }
}