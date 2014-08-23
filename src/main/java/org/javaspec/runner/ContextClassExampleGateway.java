package org.javaspec.runner;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.javaspec.dsl.Because;
import org.javaspec.dsl.Cleanup;
import org.javaspec.dsl.Establish;
import org.javaspec.dsl.It;
import org.javaspec.util.DfsSearch;

final class ContextClassExampleGateway implements ExampleGateway {
  private final Class<?> contextClass;
  
  ContextClassExampleGateway(Class<?> contextClass) {
    this.contextClass = contextClass;
  }
  
  @Override
  public List<Throwable> findInitializationErrors() {
    List<Throwable> list = new LinkedList<Throwable>();
    if(!hasAnyTests())
      list.add(new NoExamplesException(contextClass));
    if(isStepSequenceAmbiguous(Establish.class))
      list.add(new UnknownStepExecutionSequenceException(contextClass, Establish.class.getSimpleName()));
    if(isStepSequenceAmbiguous(Because.class))
      list.add(new UnknownStepExecutionSequenceException(contextClass, Because.class.getSimpleName()));
    if(isStepSequenceAmbiguous(Cleanup.class))
      list.add(new UnknownStepExecutionSequenceException(contextClass, Cleanup.class.getSimpleName()));
    return list;
  }

  @Override
  public Class<?> getContextClass() {
    return contextClass;
  }
  
  @Override
  public Context getContextRoot() {
    return new Context(contextClass);
//    throw new UnsupportedOperationException();
  }

  @Override
  public Stream<Example> getExamples() {
    List<Throwable> initializationErrors = findInitializationErrors();
    if(!initializationErrors.isEmpty()) {
      String msg = String.format("Test context %s has one or more initialization errors", contextClass.getName());
      throw new IllegalStateException(msg, initializationErrors.get(0));
    }
    
    Field arrange = onlyFieldOrNull(Establish.class);
    Field act = onlyFieldOrNull(Because.class);
    Field cleanup = onlyFieldOrNull(Cleanup.class);
    return ReflectionUtil.fieldsOfType(It.class, contextClass).map(it -> new FieldExample(arrange, act, it, cleanup));
  }
  
  private boolean hasAnyTests() {
    DfsSearch<Class<?>> search = new DfsSearch<Class<?>>(contextClass, parent -> parent.getDeclaredClasses());
    return search.anyNodeMatches(x -> ReflectionUtil.hasFieldsOfType(It.class, x));
  }
  
  private boolean isStepSequenceAmbiguous(Class<?> typeOfStep) {
    //No guarantee that reflection will sort fields by order of declaration; running them out of order could fail
    List<Field> thereCanBeOnlyOne = ReflectionUtil.fieldsOfType(typeOfStep, contextClass).collect(toList());
    return thereCanBeOnlyOne.size() > 1; 
  }
  
  private Field onlyFieldOrNull(Class<?> typeOfField) {
    List<Field> matchingFields = ReflectionUtil.fieldsOfType(typeOfField, contextClass).collect(toList());
    return matchingFields.isEmpty() ? null : matchingFields.get(0);
  }
  
  public static class UnknownStepExecutionSequenceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public UnknownStepExecutionSequenceException(Class<?> contextClass, String whatStepIsAmbiguous) {
      super(String.format("Impossible to determine running order of multiple %s functions in test context %s",
        whatStepIsAmbiguous, contextClass.getName()));
    }
  }
  
  public static class NoExamplesException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public NoExamplesException(Class<?> contextClass) {
      super(String.format("Test context %s must contain at least 1 example in an It field", contextClass.getName()));
    }
  }
}