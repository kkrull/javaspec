package info.javaspec.runner.ng;

import info.javaspec.runner.JavaSpecRunner;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * Runs tests written with JavaSpec lambdas under JUnit.
 *
 * For example, the class below results in two tests.
 * <ul>
 *   <li><code>returns_bar</code>: Creates the widget, calls foo(), and verifies that it returned "bar".</li>
 *   <li><code>prints_baz</code>: Creates the widget, calls foo(), and verifies that it wrote "baz" to the console.</li>
 * </ul>
 *
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
 *
 * Classes WidgetFooTest and its inner class foo are both <em>context classes</em>.  See ClassExampleGateway for
 * details.
 */
public class NewJavaSpecRunner extends Runner {
  public NewJavaSpecRunner(Class<?> contextClass) {
    throw new JavaSpecRunner.NoExamplesException(contextClass);
  }

  @Override
  public Description getDescription() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void run(RunNotifier notifier) {
    throw new UnsupportedOperationException();
  }
}
