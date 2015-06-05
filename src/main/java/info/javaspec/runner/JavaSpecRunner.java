package info.javaspec.runner;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JUnit test runner for specs written in lambdas and organized into context classes.
 * <p/>
 * For example, the class below contains in two tests.
 * <ul>
 * <li><code>returns_bar</code>: Creates the widget, calls foo(), and verifies that it returned "bar".</li>
 * <li><code>prints_baz</code>: Creates the widget, calls foo(), and verifies that it wrote "baz" to the console.</li>
 * </ul>
 * <p/>
 * <pre>
 * {@code
 * {@literal @RunWith(JavaSpecRunner.class)}
 * public class WidgetFooTest {
 *   public class foo {
 *     private final PrintStreamSpy printStreamSpy = new PrintStreamSpy();
 *     private Widget subject;
 *     private String returned;
 *
 *     Establish that = () -> subject = new Widget(printStreamSpy);
 *     Because of = () -> returned = subject.foo();
 *
 *     It returns_bar = () -> assertEquals("bar", returned);
 *     It prints_baz = () -> assertEquals("baz", printStreamSpy.getWhatWasPrinted());
 *   }
 * }
 * }
 * </pre>
 * <p/>
 * Classes WidgetFooTest and its inner class foo are both <em>context classes</em>.  See ClassSpecGateway for
 * details.
 */
public final class JavaSpecRunner extends Runner {
  private final SpecGateway<ClassContext> gateway;
  private final Map<String, Description> specDescriptions = new HashMap<>();

  public JavaSpecRunner(Class<?> rootContextClass) {
    this(new ClassSpecGateway(rootContextClass));
  }

  public JavaSpecRunner(SpecGateway<ClassContext> gateway) {
    this.gateway = gateway;
    if(!gateway.hasSpecs())
      throw new NoSpecs(gateway.rootContextId());
  }

  @Override
  public Description getDescription() {
    return makeSuiteDescription(gateway.rootContext());
  }

  private Description makeSuiteDescription(ClassContext context) {
    String contextDisplayName = context.displayName;
    Description suite = Description.createSuiteDescription(contextDisplayName, context.id);

    gateway.getSpecs(context).stream()
      .map(x -> makeAndMemoizeTestDescription(contextDisplayName, x))
      .forEach(suite::addChild);

    gateway.getSubcontexts(context).stream()
      .map(this::makeSuiteDescription)
      .forEach(suite::addChild);

    return suite;
  }

  private Description makeAndMemoizeTestDescription(String contextDisplayName, Spec spec) {
    Description testDescription = Description.createTestDescription(contextDisplayName, spec.displayName, spec.id);
    specDescriptions.put(spec.id, testDescription);
    return testDescription;
  }

  @Override
  public void run(RunNotifier notifier) {
    ensureTestDescriptionsMemoized();
    runContext(gateway.rootContext(), notifier);
  }

  //No guarantee that getDescription was called earlier to trigger memoization, which is required for notifications.
  private void ensureTestDescriptionsMemoized() {
    if(!specDescriptions.isEmpty())
      return;

    getDescription();
  }

  private void runContext(ClassContext context, RunNotifier notifier) {
    gateway.getSpecs(context).stream().forEach(x -> runSpecIfNotIgnored(x, notifier));
    gateway.getSubcontexts(context).stream().forEach(x -> runContext(x, notifier));
  }

  private void runSpecIfNotIgnored(Spec spec, RunNotifier notifier) {
    Description description = specDescriptions.get(spec.id);
    if(spec.isIgnored()) {
      notifier.fireTestIgnored(description);
      return;
    }

    notifier.fireTestStarted(description);
    Optional<Failure> failure = runSpec(spec, description);
    if(failure.isPresent())
      notifier.fireTestFailure(failure.get());
    else
      notifier.fireTestFinished(description);
  }

  private Optional<Failure> runSpec(Spec spec, Description description) {
    try {
      spec.run();
    } catch(Exception | AssertionError e) {
      return Optional.of(new Failure(description, e));
    }
    return Optional.empty();
  }

  @Override
  public int testCount() {
    long numSpecs = gateway.countSpecs();
    if(numSpecs > Integer.MAX_VALUE)
      throw new TooManySpecs(gateway.rootContextId(), numSpecs);
    else
      return (int) numSpecs;
  }

  public static final class NoSpecs extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoSpecs(String contextName) {
      super(String.format("Context %s must contain at least 1 spec", contextName));
    }
  }

  public static final class TooManySpecs extends RuntimeException {
    private static final String FORMAT = "Context %s has more specs than JUnit can support: %d";

    public TooManySpecs(String contextName, long numSpecs) {
      super(String.format(FORMAT, contextName, numSpecs));
    }
  }
}
