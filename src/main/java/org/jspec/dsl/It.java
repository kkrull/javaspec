package org.jspec.dsl;

/**
 * The basic building block for a single test.  Include lots of these in each test class, like so:
 * <code>
 * @RunWith(JSpecRunner.class)
 * public class WidgetFooTest {
 *   private final Widget subject;
 *   
 *   public SpecTests() {
 *     this.subject = new Widget();
 *   }
 *   
 *   It returns_bar = () -> assertEquals("bar", subject.foo());
 *   It ...
 * }
 * </code>
 */
@FunctionalInterface
public interface It {
  public void run() throws Exception;
}