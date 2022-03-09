package info.javaspec.api;

//Entrypoint for all syntax used to write specs in JavaSpec
public interface JavaSpec {
	void it(String behavior, Verification verification);
}
