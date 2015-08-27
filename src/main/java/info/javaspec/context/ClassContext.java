package info.javaspec.context;

import info.javaspec.spec.Spec;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ClassContext extends Context {
  private final Description suiteDescription;
  private final List<Spec> specs;
  private final List<Context> subContexts;

  protected ClassContext(String id, Description suiteDescription) {
    super(id);
    this.suiteDescription = suiteDescription;
    this.specs = new LinkedList<>();
    this.subContexts = new LinkedList<>();
  }

  @Override
  public void addSpec(Spec spec) {
    specs.add(spec);
    spec.addDescriptionTo(suiteDescription);
  }

  public void addSubContext(Context context) {
    subContexts.add(context);
    suiteDescription.addChild(context.getDescription());
  }

  @Override
  public Description getDescription() { return suiteDescription; }

  @Override
  public boolean hasSpecs() {
    boolean hasOwnExamples = getSpecs().findAny().isPresent();
    Stream<Boolean> childrenHaveExamples = getSubContexts().map(Context::hasSpecs);
    return hasOwnExamples || childrenHaveExamples.findAny().isPresent();
  }

  @Override
  public long numSpecs() {
    long declaredInSelf = getSpecs().count();
    long declaredInDescendants = getSubContexts()
      .map(Context::numSpecs)
      .collect(Collectors.summingLong(x -> x));

    return declaredInSelf + declaredInDescendants;
  }

  @Override
  public void run(RunNotifier notifier) {
    getSpecs().forEach(x -> x.run(notifier));
    getSubContexts().forEach(x -> x.run(notifier));
  }

  private Stream<Spec> getSpecs() { return specs.stream(); }
  private Stream<Context> getSubContexts() { return subContexts.stream(); }
}
