package org.javaspec.dsl;

/**
 * The Assert part of running a test.  Include one or more of these in each test class, like so:
 * <code>
 * @RunWith(JavaSpecRunner.class)
 * public class WidgetFooTest {
 *   private final PrintStreamSpy printStreamSpy = new PrintStreamSpy();
 *   private final String returned;
 *   
 *   public SpecTests() {
 *     Widget subject = new Widget(printStreamSpy); //NB: You can also do this in an Establish block
 *     this.returned = subject.foo(); //NB: You can also do this in a Because block
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