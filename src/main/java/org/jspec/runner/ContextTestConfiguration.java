package org.jspec.runner;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.jspec.dsl.It;

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
    List<Field> itFields = ReflectionUtil.fieldsOfType(It.class, contextClass).collect(toList());
    
    List<Throwable> list = new LinkedList<Throwable>();
    if(itFields.isEmpty()) {
      list.add(new NoExamplesException(contextClass));
    }
    return list;
  }

  @Override
  public boolean hasInitializationErrors() {
    return !findInitializationErrors().isEmpty();
  }

  @Override
  public Class<?> getContextClass() {
    return contextClass;
  }

  @Override
  public List<Example> getExamples() {
    return ReflectionUtil.fieldsOfType(It.class, contextClass).map(FieldExample::new).collect(toList());
  }
  
  public static class NoExamplesException extends Exception {
    private static final long serialVersionUID = 1L;
    public NoExamplesException(Class<?> contextClass) {
      super(String.format("Test context %s must contain at least 1 example in an It field", contextClass.getName()));
    }
  }
}