package info.javaspec.sandbox.junit;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JUnitAtomicTest {
  @Test
  public void doOneThing() throws Exception {
    assertThat(1, equalTo(1));
  }
}
