# Continuous Integration/Delivery (CI/CD) with Github Actions

This project uses Github Actions for Continuous Integration (CI) and Continuous
Delivery (CD).  See [Github Actions Syntax][github-actions-syntax] for details.

This project has the following workflows, which are defined in
`.github/workflows/`:

* `main_pull_request.yml`: Runs when a Pull Request is created or updated.  It
  builds the project, runs tests and other checks, and assembles artifacts.
* `main_push.yml`: Runs when there is any kind of merge (or direct push) back
  into `main`.  It signs and deploys SNAPSHOT artifacts to Sonatype OSSRH so
  that developers can test the entire process of fetching and using
  dependencies, with the latest version of the project.
* _To be determined_: Deploy release artifacts to a Sonatype OSSRH staging
  repository, for eventual promotion to the Maven Central Repository (i.e.
  public availability).

[github-actions-syntax]: https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions
