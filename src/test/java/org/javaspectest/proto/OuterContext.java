package org.javaspectest.proto;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.javaspec.dsl.Establish;
import org.javaspec.dsl.It;

public class OuterContext {
  private List<String> context = new LinkedList<String>();
  
  Establish arrange = () -> context.add("OuterContext::arrange");
  It asserts = () -> assertThat(context, contains("OuterContext::arrange"));
  
  public class InnerContext {
    Establish arrange = () -> context.add("InnerContext::arrange");
    It asserts = () -> assertThat(context, contains("OuterContext::arrange", "InnerContext::arrange"));
  }
}