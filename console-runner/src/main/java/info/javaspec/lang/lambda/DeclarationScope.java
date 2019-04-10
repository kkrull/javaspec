package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;
import info.javaspec.lang.lambda.Exceptions.NoSubjectDefined;

import java.util.Optional;
import java.util.Stack;

/** Groups recently-declared specs into a collection of specs that can be run together. */
final class DeclarationScope {
  private final RootCollection rootCollection;
  private final Stack<SequentialCollection> subjectCollections;

  public DeclarationScope() {
    this.rootCollection = new RootCollection();
    this.subjectCollections = new Stack<>();
  }

  public void declareSpecsFor(String subject, BehaviorDeclaration describeBehavior) {
    SequentialCollection newSubjectCollection = new SequentialCollection(subject);
    currentCollection().addSubCollection(newSubjectCollection);
    this.subjectCollections.push(newSubjectCollection);

    describeBehavior.declareSpecs();
    this.subjectCollections.pop();
  }

  public void createSpec(String intendedBehavior, BehaviorVerification verification) {
    Spec spec = new DescriptiveSpec(intendedBehavior, verification);
    subjectCollection()
      .orElseThrow(() -> NoSubjectDefined.forSpec(intendedBehavior))
      .addSpec(spec);
  }

  public SpecCollection createRootCollection() {
    return this.rootCollection;
  }

  private CompositeSpecCollection currentCollection() {
    return subjectCollection()
      .map(CompositeSpecCollection.class::cast)
      .orElse(this.rootCollection);
  }

  private Optional<SequentialCollection> subjectCollection() {
    return this.subjectCollections.isEmpty()
      ? Optional.empty()
      : Optional.of(this.subjectCollections.peek());
  }
}
