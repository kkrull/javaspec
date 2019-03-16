package info.javaspec.lang.lambda;

class ExplodingStaticInitializer {
  static {
    boolean conditionToAllowFaultyStaticInitializerToCompile = true;
    if(conditionToAllowFaultyStaticInitializerToCompile) {
      throw new RuntimeException("bang!");
    }
  }
}
