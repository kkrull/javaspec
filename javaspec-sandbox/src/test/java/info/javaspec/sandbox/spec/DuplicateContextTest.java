package info.javaspec.sandbox.spec;

import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(JavaSpecRunner.class)
public class DuplicateContextTest {
  public class given_A {
    public class when_x {
      It should_do_y = () -> assertThat(1, equalTo(1));
    }
  }

  public class given_B {
    public class when_x {
      It should_do_y = () -> assertThat(1, equalTo(1));
    }
  }
}
