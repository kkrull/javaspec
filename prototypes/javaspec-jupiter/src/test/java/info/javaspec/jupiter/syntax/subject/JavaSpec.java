package info.javaspec.jupiter.syntax.subject;

abstract class JavaSpec<S> {
  private S subject;

  protected S getSubject() {
    return this.subject;
  }

  protected void setSubject(S subject) {
    this.subject = subject;
  }
}
