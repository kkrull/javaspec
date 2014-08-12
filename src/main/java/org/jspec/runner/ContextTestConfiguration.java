package org.jspec.runner;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.jspec.dsl.It;

final class ContextTestConfiguration implements TestConfiguration {
  private final Class<?> contextClass;
  
  ContextTestConfiguration(Class<?> contextClass) {
    this.contextClass = contextClass;
  }
  
  @Override
  public List<Throwable> findInitializationErrors() {
    List<Throwable> list = new LinkedList<Throwable>();
    if(!ReflectionUtil.hasFieldsOfType(It.class, contextClass)) {
      list.add(new NoExamplesException(contextClass));
    }
    return list;
  }

  @Override
  public Class<?> getContextClass() {
    return contextClass;
  }

  @Override
  public Stream<Example> getExamples() {
    if(!ReflectionUtil.hasFieldsOfType(It.class, contextClass))
      throw new NoExamplesException(contextClass);
    
    return ReflectionUtil.fieldsOfType(It.class, contextClass).map(FieldExample::new);
  }
  
  public static class NoExamplesException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public NoExamplesException(Class<?> contextClass) {
      super(String.format("Test context %s must contain at least 1 example in an It field", contextClass.getName()));
    }
  }
}