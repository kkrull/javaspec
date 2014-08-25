package org.javaspec.runner;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.javaspec.dsl.Because;
import org.javaspec.dsl.Cleanup;
import org.javaspec.dsl.Establish;
import org.javaspec.dsl.It;
import org.javaspec.util.DfsSearch;

final class ClassExampleGateway implements ExampleGateway {
  private final Class<?> contextClass;
  
  ClassExampleGateway(Class<?> contextClass) {
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
  public List<String> getExampleNames(Context context) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Stream<NewExample> getExamples() {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Context getRootContext() {
    ContextStats contextStats = readContext(contextClass);
    return contextStats.context;
  }

  private static ContextStats readContext(Class<?> contextClass) {
    List<Class<?>> innerClasses = Stream.of(contextClass.getDeclaredClasses())
      .filter(x -> !Modifier.isStatic(x.getModifiers()))
      .collect(toList());
    boolean currentHasExamples = ReflectionUtil.hasFieldsOfType(It.class, contextClass);
    if(innerClasses.isEmpty()) {
      Context leafContext = new Context(contextClass.getSimpleName());
      return new ContextStats(leafContext, currentHasExamples);
    }
    
    List<ContextStats> subContextsWithExamples = innerClasses.stream()
      .map(ClassExampleGateway::readContext)
      .filter(x -> x.hasExamples)
      .collect(toList());
    Context internalContext = new Context(contextClass.getSimpleName(), 
      subContextsWithExamples.stream().map(x -> x.context).collect(toList()));
    boolean childrenHaveExamples = !subContextsWithExamples.isEmpty();
    return new ContextStats(internalContext, currentHasExamples || childrenHaveExamples);
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
  
//  private Field onlyFieldOrNull(Class<?> typeOfField) {
//    List<Field> matchingFields = ReflectionUtil.fieldsOfType(typeOfField, contextClass).collect(toList());
//    return matchingFields.isEmpty() ? null : matchingFields.get(0);
//  }
  
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
  
  private static class ContextStats {
    public final Context context;
    public final boolean hasExamples;
    
    public ContextStats(Context context, boolean hasExamples) {
      this.context = context;
      this.hasExamples = hasExamples;
    }
  }
}