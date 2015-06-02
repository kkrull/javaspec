package info.javaspec.runner.ng;

import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/** Reads specs from no-arg lambdas assigned to It fields in a hierarchy of context classes. */
public final class ClassSpecGateway implements SpecGateway<ClassContext> {
  private final Class<?> rootContext;

  public ClassSpecGateway(Class<?> rootContext) {
    this.rootContext = rootContext;
  }

  @Override
  public String rootContextId() {
    return rootContext().id;
  }

  @Override
  public ClassContext rootContext() {
    return makeContext(rootContext);
  }

  @Override
  public List<ClassContext> getSubcontexts(ClassContext context) {
    return readInnerClasses(context.source)
      .map(this::makeContext)
      .collect(toList());
  }

  private ClassContext makeContext(Class<?> context) {
    String declaredName = context.getSimpleName();
    String displayName = context == rootContext ? declaredName : humanize(declaredName);
    return new ClassContext(context.getName(), displayName, context);
  }

  @Override
  public boolean hasSpecs() {
    return hasSpecs(rootContext);
  }

  private boolean hasSpecs(Class<?> context) {
    boolean hasOwnExamples = readDeclaredItFields(context).findAny().isPresent();
    return hasOwnExamples || readInnerClasses(context).anyMatch(this::hasSpecs);
  }

  @Override
  public long countSpecs() {
    return countSpecs(rootContext);
  }

  private long countSpecs(Class<?> context) {
    long declaredInSelf = readDeclaredItFields(context).count();
    long declaredInDescendants = readInnerClasses(context)
      .map(this::countSpecs)
      .collect(Collectors.summingLong(x -> x));

    return declaredInSelf + declaredInDescendants;
  }

  @Override
  public List<Spec> getSpecs(ClassContext context) {
    return readDeclaredItFields(context.source)
      .map(x -> makeSpec(x, context))
      .collect(toList());
  }

  private static Spec makeSpec(Field itField, Context context) {
    Class<?> declaringClass = itField.getDeclaringClass();
    String fullyQualifiedId = String.format("%s.%s", declaringClass.getCanonicalName(), itField.getName());
    return new Spec(fullyQualifiedId, humanize(itField.getName())) {
      @Override
      public boolean isIgnored() {
        throw new UnsupportedOperationException();
      }

      @Override
      public void run() {
        throw new UnsupportedOperationException();
      }
    };
  }

  private static String humanize(String behaviorOrContext) {
    return behaviorOrContext.replace('_', ' ');
  }

  private static Stream<Field> readDeclaredItFields(Class<?> context) {
    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(It.class, context).filter(isInstanceField);
  }

  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
    Predicate<Class<?>> isNonStatic = x -> !Modifier.isStatic(x.getModifiers());
    return Stream.of(parent.getDeclaredClasses()).filter(isNonStatic);
  }
}
