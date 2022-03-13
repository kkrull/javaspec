package info.javaspec.sandbox.spec;

import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(JavaSpecRunner.class)
public class TwoLeafTest {
  It should_do_one_thing = () -> assertThat(1, equalTo(1));
  It should_do_something_else = () -> assertThat(2, equalTo(2));
}
