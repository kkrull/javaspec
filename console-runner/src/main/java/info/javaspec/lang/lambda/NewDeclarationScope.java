package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.Exceptions.NoSubjectDefined;

import java.util.Optional;
import java.util.Stack;

/** Groups recently-declared specs into a collection of specs that can be run together. */
final class NewDeclarationScope {
  private RootCollection rootCollection;
  private SequentialCollection currentSubject;

  public NewDeclarationScope() {
    this.rootCollection = new RootCollection();
  }

  public void declareSpecsFor(String subject, BehaviorDeclaration describeBehavior) {
    this.currentSubject = new SequentialCollection(subject);
    this.rootCollection.addSubCollection(this.currentSubject);
    describeBehavior.declareSpecs();
  }

  public void createSpec(String intendedBehavior, BehaviorVerification verification) {
    DescriptiveSpec spec = new DescriptiveSpec(intendedBehavior, verification);
    Optional.ofNullable(this.currentSubject)
      .orElseThrow(() -> NoSubjectDefined.forSpec(intendedBehavior))
      .addSpec(spec);
  }

  public SpecCollection createRootCollection() {
    return this.rootCollection;
  }
}
