package org.jspec.dsl;

/**
 * The basic building block for a single test.  Include one or more of these in each test class, like so:
 * <code>
 * @RunWith(JSpecRunner.class)
 * public class WidgetFooTest {
 *   private final PrintStreamSpy printStreamSpy = new PrintStreamSpy();
 *   private final String returned;
 *   
 *   public SpecTests() {
 *     Widget subject = new Widget(printStreamSpy);
 *     this.returned = subject.foo();
 *   }
 *   
 *   It returns_bar = () -> assertEquals("bar", returned);
 *   It prints_baz = () -> assertEquals("baz", printStreamSpy.getWhatWasPrinted());
 * }
 * </code>
 */
@FunctionalInterface
public interface It {
  public void run() throws Exception;
}