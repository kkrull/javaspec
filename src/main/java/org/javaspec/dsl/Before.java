package org.javaspec.dsl;

@FunctionalInterface
public interface Before {
  public void run() throws Exception;
}