# Design Log

## Design Principles

A few thoughts collected along the way, as new features are completed:

1. **Specs should be descriptive**: I was hesitant to require a description on all `Spec` implementations.  I decided
to make it a requirement after realizing -- _the whole point of this library is to make it easier to describe things_.
1. **Specs should be organized**: It is currently allowed to declare `it` anywhere, without saying what you are
describing.  I don't think that makes much sense; developers should say what they are writing tests for.  With that
in mind, it makes more sense for the eventual root suite that collects all the `describe` blocks to only allow child
suites, not specs.


I will leave you now, Adventurer, with these words of advice:

> Go forth and solve a problem, without creating any new ones to take its place.

May you be successful in your pursuit.
