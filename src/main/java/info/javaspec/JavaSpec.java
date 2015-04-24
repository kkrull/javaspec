package info.javaspec;

import java.io.PrintStream;

/**
 * Command-line interface for JavaSpec.  <em>For diagnostic purposes only</em>.
 * <p>
 * See JavaSpecRunner for details on running tests.
 */
public final class JavaSpec {
  private final PrintStream console;
  private final ExitHandler system;
  private final AppConfigGateway configGateway;

  /* Command line interface */
  
  public static void main(String... args) {
    main(System.out, System::exit, args);
  }

  public static void main(PrintStream console, ExitHandler system, String... args) {
    JavaSpec cli = new JavaSpec(console, system);
    cli.run(args);
  }

  public JavaSpec(PrintStream console, ExitHandler system) {
    this.console = console;
    this.system = system;
    this.configGateway = new AppConfigGateway();
  }

  public void run(String... args) {
    if(isHelpCommand(args))
      printUsage(0);
    else if(isVersionCommand(args))
      printVersion();
    else
      printUsage(1);
  }

  private static boolean isHelpCommand(String... args) {
    return args.length == 0 || (args.length == 1 && "--help".equals(args[0]));
  }

  private void printUsage(int exitCode) {
    console.println(String.format("Usage: java %s --version", getClass().getName()));
    console.println("--help: Show this help");
    console.println("--version: Show the version");
    system.exit(exitCode);
  }

  private static boolean isVersionCommand(String... args) {
    return args.length == 1 && "--version".equals(args[0]);
  }

  private void printVersion() {
    String version = configGateway.version();
    console.println(version);
    system.exit(0);
  }

  @FunctionalInterface
  public interface ExitHandler {
    void exit(int code);
  }
}