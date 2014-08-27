package org.javaspec.runner;

import java.lang.reflect.Field;

final class ContextExample implements NewExample {
  private final Field it;

  public ContextExample(Field it) {
    this.it = it;
  }

  @Override
  public String getContextName() {
    throw new UnsupportedOperationException();
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