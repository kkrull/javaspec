package info.javaspec.runner;

import info.javaspec.context.Context;
import info.javaspec.context.ContextFactory;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * JUnit test runner for specs written in lambdas and organized into context classes.
 * 
 * For example, the class below contains in two tests.
 * <ul>
 * <li><code>returns_bar</code>: Creates the widget, calls foo(), and verifies that it returned "bar".</li>
 * <li><code>prints_baz</code>: Creates the widget, calls foo(), and verifies that it wrote "baz" to the console.</li>
 * </ul>
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
 * <p>
 * Classes WidgetFooTest and its inner class foo are both <em>context classes</em>.  See ClassSpecGateway for
 * details.
 */
public final class JavaSpecRunner extends Runner {
  private Context rootContext;

  public JavaSpecRunner(Class<?> rootContextClass) {
    this(ContextFactory.createRootContext(rootContextClass));
  }

  public JavaSpecRunner(Context rootContext) {
    this.rootContext = rootContext;

    if(!rootContext.hasSpecs())
      throw NoSpecs.forContext(rootContext.getId());
  }

  @Override
  public Description getDescription() {
    return rootContext.getDescription();
  }

  @Override
  public void run(RunNotifier notifier) {
    rootContext.run(notifier);
  }

  @Override
  public int testCount() {
    long numSpecs = rootContext.numSpecs();
    if(numSpecs > Integer.MAX_VALUE)
      throw TooManySpecs.forContext(rootContext.getId(), numSpecs);
    else
      return (int)numSpecs;
  }

  public static final class NoSpecs extends RuntimeException {
    public static NoSpecs forContext(String id) {
      return new NoSpecs(String.format("Context %s must contain at least 1 spec", id));
    }

    private NoSpecs(String message) { super(message); }
  }

  public static final class TooManySpecs extends RuntimeException {
    public static TooManySpecs forContext(String id, long numSpecs) {
      String message = String.format("Context %s has more specs than JUnit can support: %d", id, numSpecs);
      return new TooManySpecs(message);
    }

    private TooManySpecs(String message) { super(message); }
  }
}
