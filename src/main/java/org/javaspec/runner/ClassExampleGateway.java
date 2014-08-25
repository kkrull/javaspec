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
//    Field arrange = onlyFieldOrNull(Establish.class);
//    Field act = onlyFieldOrNull(Because.class);
//    Field cleanup = onlyFieldOrNull(Cleanup.class);
    return ReflectionUtil.fieldsOfType(It.class, contextClass).map(it -> new ContextExample(it));
  }
  
  @Override
  public Context getRootContext() {
    ContextStats contextStats = readContext(contextClass);
    return contextStats.context;
  }

  private static ContextStats readContext(Class<?> contextClass) {
    Stream<Class<?>> innerClasses = Stream.of(contextClass.getDeclaredClasses())
      .filter(x -> !Modifier.isStatic(x.getModifiers()));
    Stream<Context> subContexts = innerClasses
      .map(ClassExampleGateway::readContext)
      .filter(x -> x.hasExamples)
      .map(x -> x.context);
    
    Context context = new Context(contextClass.getSimpleName(), subContexts.collect(toList()));
    boolean contextDeclaresExamples = ReflectionUtil.hasFieldsOfType(It.class, contextClass);
    return new ContextStats(context, contextDeclaresExamples || context.hasChildren());
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
  
  private static class ContextStats {
    public final Context context;
    public final boolean hasExamples;
    
    public ContextStats(Context context, boolean hasExamples) {
      this.context = context;
      this.hasExamples = hasExamples;
    }
  }
}