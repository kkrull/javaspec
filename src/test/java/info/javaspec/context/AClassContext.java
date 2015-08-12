package info.javaspec.context;

import info.javaspec.spec.Spec;

import static com.google.common.collect.Lists.newArrayList;

public final class AClassContext {
  public static ClassContext of(Class<?> source) {
    return ClassContext.createRootContext(source);
  }

  public static ClassContext withSpecs(Spec... specs) {
    return new ClassContext("withSpecs", "withSpecs", newArrayList(specs), newArrayList());
  }

  public static ClassContext withSubContexts(Context... subContexts) {
    return new ClassContext("withSubContexts", "withSubContexts", newArrayList(), newArrayList(subContexts));
  }
}
