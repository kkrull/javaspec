package org.jspec.runner;

import java.util.List;

final class ContextTestConfiguration implements TestConfiguration {

  public static ContextTestConfiguration forClass(Class<?> contextClass) {
    return new ContextTestConfiguration();
  }
  
  @Override
  public List<Throwable> findInitializationErrors() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasInitializationErrors() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Class<?> getContextClass() {
    throw new UnsupportedOperationException();
  }
}