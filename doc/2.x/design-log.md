# Design Log

This document captures some of the thoughts and guiding principles behind JavaSpec's, in hopes of
helping future developers understand the design, or to remind myself what I had in mind as I went
along.

1. **Someone has already figured this out**:
   Other languages have different ways of organizing tests that are popular in their respective
   communities and that may be applicable here.  Testing in Java doesn't have to be different just
   for the sake of being different, and we don't have to be stuck with a hard to use syntax just
   because the idea hasn't caught on in the Java community, yet.

1. **Use plain language to describe things**:
   Describing behavior with Java class and method names is so infuriatingly tedious that it limits a
   developer's ability to say what they mean.  So JavaSpec borrows a popular idea from lots of other
   testing libraries: _Just use a String!_

1. **Specs should be organized**:
   You should be able to describe a thing, its expected behavior, and the conditions or context
   where that behavior applies.

1. **Specs should be descriptive**:
   _The whole point of this library is to make it easier to describe things_.  You do have to
   describe _something_, so specs require a string saying what the expectation is, and specs need to
   be declared within a `describe` block of some sort so the rest of the world can tell what you're
   talking about.

1. **Users decide how to organize their own specs**:
   You know your code; it's _your_ responsibility to figure out how to organize your specs in a
   manner that _you_ consider organized and maintainable.

   For example, some libraries like [ExUnit][exunit-describe] do not allow you to nest `describe`
   blocks.  That is often a refreshing and helpful design constraint that helps you avoid an
   unnecessarily confusing hierarchy of code that applies to some–but not all–of the specs in the
   file.  But sometimes it would be really helpful to an inner `describe` block, if only to limit
   the context where some expected behavior applies.

   JavaSpec does have a few requirements, but after that how you organize your specs is up to you.


I will leave you now, Adventurer, with these words of advice:

> Go forth and solve a problem, without creating any new ones to take its place.

May you be successful in your pursuit.


[exunit-describe]: https://hexdocs.pm/ex_unit/ExUnit.Case.html#module-module-and-describe-tags

