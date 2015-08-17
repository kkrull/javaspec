package info.javaspec.context;

import info.javaspec.spec.Spec;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassContext extends Context {
  private final String displayName;
  private final List<Spec> specs;
  private final List<Context> subContexts;

  protected ClassContext(String id, String displayName, List<Spec> specs, List<Context> subContexts) {
    super(id);
    this.displayName = displayName;
    this.specs = specs;
    this.subContexts = subContexts;
  }

  private String getDisplayName() { return displayName; }
  private Stream<Spec> getSpecs() { return specs.stream(); }
  private Stream<Context> getSubContexts() { return subContexts.stream(); }

  @Override
  public Description getDescription() {
    Description suite = Description.createSuiteDescription(getDisplayName(), getId());
    getSpecs().forEach(x -> x.addDescriptionTo(suite));
    getSubContexts().map(Context::getDescription).forEach(suite::addChild);
    return suite;
  }

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
    getSpecs().forEach(x -> runSpec(x, notifier));
    getSubContexts().forEach(x -> x.run(notifier));
  }

  private void runSpec(Spec spec, RunNotifier notifier) {
    notifier.fireTestStarted(null);
    try {
      spec.run();
    } catch(Exception e) {
      throw new RuntimeException("Failed to run spec", e);
    }
  }
}
