package info.javaspecproto;

import info.javaspec.dsl.Cleanup;
import info.javaspec.dsl.Establish;
import info.javaspec.dsl.It;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class OuterContext {
  private List<String> context = new LinkedList<String>();
  private boolean innerCleanupDone = false;

  Establish arrange = () -> context.add("OuterContext::arrange");
  Cleanup outer_cleanup = () -> {
    assertThat(innerCleanupDone, equalTo(context.contains("InnerContext::arrange")));
  };

  It asserts = () -> assertThat(context, contains("OuterContext::arrange"));

  public class InnerContext {
    Establish arrange = () -> context.add("InnerContext::arrange");
    It asserts = () -> assertThat(context, contains("OuterContext::arrange", "InnerContext::arrange"));
    Cleanup inner_cleanup = () -> innerCleanupDone = true;
  }
}