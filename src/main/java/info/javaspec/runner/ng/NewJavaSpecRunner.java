package info.javaspec.runner.ng;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.util.List;

/**
 * Runs tests written with JavaSpec lambdas under JUnit.
 * <p/>
 * For example, the class below results in two tests.
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
 * Classes WidgetFooTest and its inner class foo are both <em>context classes</em>.  See ClassExampleGateway for
 * details.
 */
public final class NewJavaSpecRunner extends Runner {
  private final NewExampleGateway gateway;

  public NewJavaSpecRunner(Class<?> contextClass) {
    throw new UnsupportedOperationException("TODO KDK: Make a real ClassExampleGateway");
  }

  public NewJavaSpecRunner(NewExampleGateway gateway) {
    this.gateway = gateway;

    if(!gateway.hasExamples())
      throw new NoExamplesException(gateway.rootContextClass());
  }

  @Override
  public Description getDescription() {
    return new DescriptionFactory(gateway.rootContextClass()).makeDescriptionTree();
  }

  @Override
  public void run(RunNotifier notifier) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int testCount() {
    return gateway.totalNumExamples();
  }

  public static class NoExamplesException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoExamplesException(Class<?> context) {
      super(String.format("Context class %s must contain at least 1 example in an It field", context.getName()));
    }
  }

  private class DescriptionFactory {
    private final Class<?> contextClass;
    private final List<String> exampleFieldNames;
    private final List<Class<?>> subContextClasses;

    public DescriptionFactory(Class<?> contextClass) {
      this.contextClass = contextClass;
      this.exampleFieldNames = gateway.exampleFieldNames(contextClass);
      this.subContextClasses = gateway.subContextClasses(contextClass);
    }

    public Description makeDescriptionTree() {
      if(isSingletonTest())
        return makeTestDescription(exampleFieldNames.get(0));

      final Description suite = Description.createSuiteDescription(contextClass);
      exampleFieldNames.stream().map(this::makeTestDescription).forEach(suite::addChild);
      subContextClasses.stream().map(x -> new DescriptionFactory(x).makeDescriptionTree()).forEach(suite::addChild);
      return suite;
    }

    private boolean isSingletonTest() {
      boolean isLeafContext = subContextClasses.isEmpty();
      return isLeafContext && exampleFieldNames.size() == 1;
    }

    private Description makeTestDescription(String exampleName) {
      return Description.createTestDescription(contextClass, exampleName);
    }
  }
}
