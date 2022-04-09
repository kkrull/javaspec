package info.javaspec.client;

public class Greeter {
	public String greet() {
		return "Hello world!";
	}

	public String greet(String name) {
		return String.format("Hello %s!", name);
	}
}
