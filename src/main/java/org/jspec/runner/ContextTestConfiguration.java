package org.jspec.runner;

import java.util.List;

final class ContextTestConfiguration implements TestConfiguration {

  private final Class<?> contextClass;
  
  public static ContextTestConfiguration forClass(Class<?> contextClass) {
    return new ContextTestConfiguration(contextClass);
  }
  
  private ContextTestConfiguration(Class<?> contextClass) {
    this.contextClass = contextClass;
  }
  
  @Override
  public List<Throwable> findInitializationErrors() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasInitializationErrors() {
    return false;
  }

  @Override
  public Class<?> getContextClass() {
    return contextClass;
  }
}