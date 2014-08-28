package org.javaspec.dsl;

/** A thunk that executes the Arrange step of a test.  Runs first. */
@FunctionalInterface
public interface Establish extends Before { }