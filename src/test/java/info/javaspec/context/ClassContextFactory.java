package info.javaspec.context;

import info.javaspec.spec.Spec;

import static com.google.common.collect.Lists.newArrayList;

public final class ClassContextFactory {

  public static ClassContext classContextWithSpecs(Spec... specs) {
    return new ClassContext("classContextWithSpecs", "classContextWithSpecs", newArrayList(specs), newArrayList());
  }

  public static ClassContext classContextWithSubContexts(Context... subContexts) {
    return new ClassContext("classContextWithSubContexts", "classContextWithSubContexts", newArrayList(), newArrayList(subContexts));
  }
}
