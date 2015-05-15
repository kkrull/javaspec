package info.javaspec.runner.ng;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

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
public class NewJavaSpecRunner extends Runner {
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
    if(gateway.totalExamples() == 1 && gateway.subContextClasses(gateway.rootContextClass()).isEmpty()) { //isSingletonTest
      String exampleName = gateway.rootContextExampleNames().get(0);
      return Description.createTestDescription(gateway.rootContextClass(), exampleName);
    }

    //TODO KDK: Build a tree of suite/test descriptions
    final Description suiteDescription = Description.createSuiteDescription(gateway.rootContextClass());
    gateway.rootContextExampleNames().stream().forEach(x -> {
      Description test = Description.createTestDescription(gateway.rootContextClass(), x);
      suiteDescription.addChild(test);
    });

    return suiteDescription;
  }

  @Override
  public void run(RunNotifier notifier) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int testCount() {
    return gateway.totalExamples();
  }

  public static class NoExamplesException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoExamplesException(Class<?> context) {
      super(String.format("Context class %s must contain at least 1 example in an It field", context.getName()));
    }
  }
}
