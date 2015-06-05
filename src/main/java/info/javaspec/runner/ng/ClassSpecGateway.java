package info.javaspec.runner.ng;

import info.javaspec.dsl.Because;
import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;
import info.javaspec.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/** Reads specs from no-arg lambdas assigned to It fields in a hierarchy of context classes. */
public final class ClassSpecGateway implements SpecGateway<ClassContext> {
  private final Class<?> rootContext;
  private final FieldSpecFactory specFactory;

  public ClassSpecGateway(Class<?> rootContext) {
    this(rootContext, FieldSpec::new);
  }

  public ClassSpecGateway(Class<?> rootContext, FieldSpecFactory specFactory) {
    this.rootContext = rootContext;
    this.specFactory = specFactory;
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

  private Spec makeSpec(Field itField, ClassContext context) {
    List<Field> befores = new ArrayList<>();
    onlyDeclaredField(context.source, Establish.class).ifPresent(befores::add);
    onlyDeclaredField(context.source, Because.class).ifPresent(befores::add);

    List<Field> afters = new ArrayList<>();
    onlyDeclaredField(context.source, Cleanup.class).ifPresent(afters::add);

    Class<?> declaringClass = itField.getDeclaringClass();
    String fullyQualifiedId = String.format("%s.%s", declaringClass.getCanonicalName(), itField.getName());
    return specFactory.makeSpec(fullyQualifiedId, humanize(itField.getName()), itField, befores, afters);
  }

  @FunctionalInterface
  interface FieldSpecFactory {
    Spec makeSpec(String id, String displayName, Field it, List<Field> befores, List<Field> afters);
  }

  private static String humanize(String behaviorOrContext) {
    return behaviorOrContext.replace('_', ' ');
  }

  private static Optional<Field> onlyDeclaredField(Class<?> context, Class<?> fieldType) {
    List<Field> fields = readDeclaredFields(context, fieldType).limit(2).collect(toList());
    switch(fields.size()) {
      case 0: return Optional.empty();
      case 1: return Optional.of(fields.get(0));
      default: throw new AmbiguousSpecFixture(context, fieldType);
    }
  }

  private static Stream<Field> readDeclaredItFields(Class<?> context) {
    return readDeclaredFields(context, It.class);
  }

  private static Stream<Field> readDeclaredFields(Class<?> context, Class<?> fieldType) {
    Predicate<Field> isInstanceField = x -> !Modifier.isStatic(x.getModifiers());
    return ReflectionUtil.fieldsOfType(fieldType, context).filter(isInstanceField);
  }

  private static Stream<Class<?>> readInnerClasses(Class<?> parent) {
    Predicate<Class<?>> isNonStatic = x -> !Modifier.isStatic(x.getModifiers());
    return Stream.of(parent.getDeclaredClasses()).filter(isNonStatic);
  }

  public static final class AmbiguousSpecFixture extends RuntimeException {
    public AmbiguousSpecFixture(Class<?> contextClass, Class<?> fieldClass) {
      super(String.format("Only 1 field of type %s is allowed in context class %s",
        fieldClass.getSimpleName(),
        contextClass));
    }
  }
}
