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
  private final ExampleFactory factory;
  
  ClassExampleGateway(Class<?> contextClass) {
    this(contextClass, ClassExampleGateway::makeExample);
  }
  
  ClassExampleGateway(Class<?> contextClass, ExampleFactory factory) {
    this.contextClass = contextClass;
    this.factory = factory;
  }
  
  private static NewExample makeExample(Class<?> contextClass, Field it) {
    return new ContextExample(nameContext(contextClass), it);
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
  
  private boolean isStepSequenceAmbiguous(Class<?> typeOfStep) { //TODO KDK: Search the context class and all its inner, context classes
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
    return readContext(contextClass);
  }
  
  @Override
  public String getRootContextName() {
    return getRootContext().name;
  }
  
  @Override
  public List<Context> getSubContexts(Context context) {
    return readInnerClasses((Class<?>) context.id)
      .filter(x -> treeContainsItField(x))
      .map(ClassExampleGateway::readContext)
      .collect(toList());
  }
  
  private static boolean treeContainsItField(Class<?> subtreeRoot) {
    DfsSearch<Class<?>> searchForItFields = new DfsSearch<Class<?>>(subtreeRoot, ClassExampleGateway::readInnerClasses);
    return searchForItFields.anyNodeMatches(x -> ReflectionUtil.hasFieldsOfType(It.class, x));
  }
  
  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
    return Stream.of(parent.getDeclaredClasses()).filter(x -> !Modifier.isStatic(x.getModifiers()));
  }
  
  private static Context readContext(Class<?> contextClass) {
    List<String> examples = ReflectionUtil.fieldsOfType(It.class, contextClass).map(Field::getName).collect(toList());
    return new Context(contextClass, nameContext(contextClass), examples);
  }
  
  private static String nameContext(Class<?> contextClass) {
    return contextClass.getSimpleName();
  }
  
  /* Examples */
  
  @Override
  public Stream<NewExample> getExamples() {
    List<NewExample> examples = new LinkedList<NewExample>();
    appendExamples(contextClass, examples);
    return examples.stream();
  }
  
  @Override
  public boolean hasExamples() {
    return getExamples().anyMatch(x -> true);
  }
  
  private void appendExamples(Class<?> contextClass, List<NewExample> examples) {
    ReflectionUtil.fieldsOfType(It.class, contextClass)
      .map(it -> factory.makeExample(contextClass, it))
      .forEach(examples::add);
    readInnerClasses(contextClass).forEach(x -> appendExamples(x, examples));
  }
}