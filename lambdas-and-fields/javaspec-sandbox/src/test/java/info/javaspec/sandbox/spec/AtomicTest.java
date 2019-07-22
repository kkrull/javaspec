package info.javaspec.sandbox.spec;

import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(JavaSpecRunner.class)
public class AtomicTest {
  It should_run = () -> assertThat(1, equalTo(1));
}
