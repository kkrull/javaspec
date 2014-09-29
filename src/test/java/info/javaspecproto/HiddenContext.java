package info.javaspecproto;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import info.javaspec.dsl.It;
import info.javaspec.runner.JavaSpecRunner;

import org.junit.runner.RunWith;

@RunWith(JavaSpecRunner.class)
class HiddenContext {
  @SuppressWarnings("unused") //Gets used via reflection
  private class hiddenInner {
    It runs = () -> assertThat(1, equalTo(1)); 
  }
}