package info.javaspec.sandbox.junit;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JUnitTwoLeafTest {
  @Test
  public void doOneThing() throws Exception {
    assertThat(1, equalTo(1));
  }

  @Test
  public void doSomethingElse() throws Exception {
    assertThat(2, equalTo(2));
  }
}
