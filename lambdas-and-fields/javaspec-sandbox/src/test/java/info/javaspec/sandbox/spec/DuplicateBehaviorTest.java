package info.javaspec.sandbox.spec;

import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(JavaSpecRunner.class)
public class DuplicateBehaviorTest {
  public class given_one_input {
    It should_always_do_x = () -> assertThat(1, equalTo(1));
  }

  public class given_some_other_input {
    It should_always_do_x = () -> assertThat(2, equalTo(2));
  }
}
