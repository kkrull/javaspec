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

final class ClassExampleGateway implements ExampleGateway {
  private final Class<?> contextClass;
  
  ClassExampleGateway(Class<?> contextClass) {
    this.contextClass = contextClass;
  }
  
  /* Validation */
  
  @Override
  public List<Throwable> findInitializationErrors() {
    List<Throwable> list = new LinkedList<Throwable>();
    if(isStepSequenceAmbiguous(Establish.class))
      list.add(new UnknownStepExecutionSequenceException(contextClass, Establish.class.getSimpleName()));
    if(isStepSequenceAmbiguous(Because.class))
      list.add(new UnknownStepExecutionSequenceException(contextClass, Because.class.getSimpleName()));
    if(isStepSequenceAmbiguous(Cleanup.class))
      list.add(new UnknownStepExecutionSequenceException(contextClass, Cleanup.class.getSimpleName()));
    
    return list;
  }
  
  private boolean isStepSequenceAmbiguous(Class<?> typeOfStep) {
    //No guarantee that reflection will sort fields by order of declaration; running them out of order could fail
    List<Field> thereCanBeOnlyOne = ReflectionUtil.fieldsOfType(typeOfStep, contextClass).collect(toList());
    return thereCanBeOnlyOne.size() > 1; 
  }
  
  public static class UnknownStepExecutionSequenceException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public UnknownStepExecutionSequenceException(Class<?> contextClass, String whatStepIsAmbiguous) {
      super(String.format("Impossible to determine running order of multiple %s functions in test context %s",
        whatStepIsAmbiguous, contextClass.getName()));
    }
  }
  
  /* Context */

  @Override
  public Context getRootContext() {
    ContextStats contextStats = readContext(contextClass);
    return contextStats.context;
  }
  
  @Override
  public String getRootContextName() {
    return getRootContext().name;
  }
  
  private static ContextStats readContext(Class<?> contextClass) {
    Stream<Class<?>> innerClasses = Stream.of(contextClass.getDeclaredClasses())
      .filter(x -> !Modifier.isStatic(x.getModifiers()));
    Stream<Context> subContexts = innerClasses
      .map(ClassExampleGateway::readContext)
      .filter(x -> x.hasExamples)
      .map(x -> x.context);
    List<String> declaredExamples = ReflectionUtil.fieldsOfType(It.class, contextClass)
      .map(Field::getName)
      .collect(toList());
    
    Context context = new Context(contextClass.getSimpleName(), declaredExamples);
    return new ContextStats(context, !declaredExamples.isEmpty() /*|| context.hasSubContexts()*/);
  }
  
  private static class ContextStats {
    public final Context context;
    public final boolean hasExamples;
    
    public ContextStats(Context context, boolean hasExamples) {
      this.context = context;
      this.hasExamples = hasExamples;
    }
  }
  
  /* Examples */
  
  @Override
  public Stream<NewExample> getExamples() {
//    Field arrange = onlyFieldOrNull(Establish.class);
//    Field act = onlyFieldOrNull(Because.class);
//    Field cleanup = onlyFieldOrNull(Cleanup.class);
//    Function<Class<?>, Class<?>[]> getInnerClasses = parent -> Stream.of(parent.getDeclaredClasses())
//      .filter(x -> !Modifier.isStatic(x.getModifiers()));
    
//    DfsSearch<Class<?>> dfs = new DfsSearch<Class<?>>(contextClass, getInnerClasses);
    
    return ReflectionUtil.fieldsOfType(It.class, contextClass).map(it -> new ContextExample(it));
  }

  @Override
  public boolean hasExamples() {
    return getExamples().anyMatch(x -> true);
  }

  @Override
  public List<Context> getSubContexts(Context context) {
    throw new UnsupportedOperationException();
  }
}