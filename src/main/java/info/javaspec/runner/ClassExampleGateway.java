package info.javaspec.runner;

import info.javaspec.dsl.Because;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;
import info.javaspec.util.DfsSearch;
import info.javaspec.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

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
  
  @FunctionalInterface
  interface ExampleFactory {
    Example makeExample(Class<?> contextClass, Field it, List<Field> runBefore, List<Field> runAfter);
  }
  
  private static Example makeExample(Class<?> contextClass, Field it, List<Field> runBefore, List<Field> runAfter) {
    return new FieldExample(nameContext(contextClass), it, runBefore, runAfter);
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
    DfsSearch<Class<?>> contextSearch = new DfsSearch<Class<?>> (contextClass, ClassExampleGateway::readInnerClasses);
    return contextSearch.anyNodeMatches(contextClass -> {
      List<Field> thereCanBeOnlyOne = ReflectionUtil.fieldsOfType(typeOfStep, contextClass).collect(toList());
      return thereCanBeOnlyOne.size() > 1; 
    });
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
  public Set<Context> getSubContexts(Context context) {
    return readInnerClasses((Class<?>) context.id)
      .filter(x -> treeContainsItField(x))
      .map(ClassExampleGateway::readContext)
      .collect(toSet());
  }
  
  private static boolean treeContainsItField(Class<?> subtreeRoot) {
    DfsSearch<Class<?>> searchForItFields = new DfsSearch<Class<?>>(subtreeRoot, ClassExampleGateway::readInnerClasses);
    return searchForItFields.anyNodeMatches(x -> ReflectionUtil.hasFieldsOfType(It.class, x));
  }
  
  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
    return Stream.of(parent.getDeclaredClasses()).filter(x -> !Modifier.isStatic(x.getModifiers()));
  }
  
  private static Context readContext(Class<?> contextClass) {
    Set<String> examples = ReflectionUtil.fieldsOfType(It.class, contextClass)
      .map(Field::getName)
      .collect(toSet());
    return new Context(contextClass, nameContext(contextClass), examples);
  }
  
  private static String nameContext(Class<?> contextClass) {
    return contextClass.getSimpleName();
  }
  
  /* Examples */
  
  @Override
  public Stream<Example> getExamples() {
    ExampleWalker tree = new ExampleWalker(factory);
    return tree.dfsTraversal(contextClass);
  }
  
  @Override
  public boolean hasExamples() {
    return getExamples().anyMatch(x -> true);
  }
  
  private static class ExampleWalker {
    private final ExampleFactory factory;
    private final List<Example> examples;
    
    public ExampleWalker(ExampleFactory factory) {
      this.factory = factory;
      this.examples = new LinkedList<Example>();
    }
    
    public Stream<Example> dfsTraversal(Class<?> rootContext) {
      appendExamples(rootContext, new ArrayList<Field>(), new ArrayList<Field>());
      return examples.stream();
    }
    
    private void appendExamples(Class<?> context, List<Field> ancestorBefores, List<Field> ancestorAfters) {
      List<Field> befores = outsideInBefores(context, ancestorBefores);
      List<Field> afters = insideOutAfters(context, ancestorAfters);
      ReflectionUtil.fieldsOfType(It.class, context)
        .map(it -> factory.makeExample(context, it, befores, afters))
        .forEach(examples::add);
      readInnerClasses(context).forEach(subcontext -> appendExamples(subcontext, befores, afters));
    }
    
    private static List<Field> outsideInBefores(Class<?> contextClass, List<Field> ancestors) {
      List<Field> befores = new ArrayList<Field>(ancestors);
      ReflectionUtil.fieldsOfType(Establish.class, contextClass).forEach(befores::add);
      ReflectionUtil.fieldsOfType(Because.class, contextClass).forEach(befores::add);
      return befores;
    }
    
    private static List<Field> insideOutAfters(Class<?> contextClass, List<Field> ancestors) {
      List<Field> afters = new ArrayList<Field>();
      ReflectionUtil.fieldsOfType(Cleanup.class, contextClass).forEach(afters::add);
      afters.addAll(ancestors);
      return afters;
    }
  }
}