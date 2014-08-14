package org.jspec.runner;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.jspec.dsl.Establish;
import org.jspec.dsl.It;

final class ContextTestConfiguration implements TestConfiguration {
  private final Class<?> contextClass;
  
  ContextTestConfiguration(Class<?> contextClass) {
    this.contextClass = contextClass;
  }
  
  @Override
  public List<Throwable> findInitializationErrors() {
    List<Throwable> list = new LinkedList<Throwable>();
    if(!ReflectionUtil.hasFieldsOfType(It.class, contextClass))
      list.add(new NoExamplesException(contextClass));
    
    List<Field> establishFields = ReflectionUtil.fieldsOfType(Establish.class, contextClass).collect(toList());
    if(establishFields.size() > 1) 
      list.add(new MultipleSetupFunctionsException(contextClass));
    
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
    
    List<Field> establishFields = ReflectionUtil.fieldsOfType(Establish.class, contextClass).collect(toList());
    Field establish = establishFields.isEmpty() ? null : establishFields.get(0);
    return ReflectionUtil.fieldsOfType(It.class, contextClass).map(it -> new FieldExample(establish, it));
  }
  
  public static class MultipleSetupFunctionsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public MultipleSetupFunctionsException(Class<?> contextClass) {
      super(String.format("", contextClass.getName()));
    }
  }
  
  public static class NoExamplesException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public NoExamplesException(Class<?> contextClass) {
      super(String.format("Test context %s must contain at least 1 example in an It field", contextClass.getName()));
    }
  }
}