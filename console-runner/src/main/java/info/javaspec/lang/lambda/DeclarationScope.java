package info.javaspec.lang.lambda;

import info.javaspec.Spec;
import info.javaspec.SpecCollection;

import java.util.Optional;
import java.util.Stack;

/** Groups recently-declared specs into a collection of specs that can be run together. */
final class DeclarationScope {
  private final Stack<WritableSpecCollection> collections;

  public DeclarationScope() {
    this.collections = new Stack<>();
    this.collections.push(new RootCollection());
  }

  public void declareSpecsFor(String subject, BehaviorDeclaration describeBehavior) {
    SequentialCollection newSubjectCollection = new SequentialCollection(subject);

    //Add the child collection in line with any other declared specs
    leafCollection().get().addSubCollection(newSubjectCollection);

    //Push on to the stack in case there are nested describes
    this.collections.push(newSubjectCollection);

    describeBehavior.declareSpecs();
    this.collections.pop();
  }

  public void createSpec(String intendedBehavior, BehaviorVerification verification) {
    Spec spec = new DescriptiveSpec(intendedBehavior, verification);
    subjectCollection()
      .orElseThrow(() -> Exceptions.NoSubjectDefined.forSpec(intendedBehavior))
      .addSpec(spec);
  }

  public SpecCollection createRootCollection() {
    SpecCollection rootCollection = this.collections.pop();
    if(!this.collections.isEmpty())
      throw new IllegalStateException("Spec declaration ended prematurely");

    return rootCollection;
  }

  private Optional<WritableSpecCollection> subjectCollection() {
    return this.leafCollection()
      .filter(x -> !RootCollection.class.equals(x.getClass()));
  }

  private Optional<WritableSpecCollection> leafCollection() {
    return this.collections.empty()
      ? Optional.empty()
      : Optional.of(this.collections.peek());
  }
}
