package info.javaspec.jupiter;

public class Greeter {
  public String makeGreeting() {
    return "Hello world!";
  }

  public String makeGreeting(String name) {
    return String.format("Hello %s!", name);
  }
}
