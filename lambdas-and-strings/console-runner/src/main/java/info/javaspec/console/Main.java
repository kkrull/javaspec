package info.javaspec.console;

import info.javaspec.console.help.HelpParameters;
import info.javaspec.lang.lambda.RunParameters;

import java.util.Arrays;
import java.util.List;

public final class Main {
  private final Reporter reporter;
  private final ExitHandler system;

  public static void main(String... args) {
    main(
      new StaticReporterFactory(System.out),
      System::exit,
      args
    );
  }

  static void main(ReporterFactory reporterFactory, ExitHandler system, String... args) {
    ArgumentParser cliParser = cliArgumentParser(new StaticCommandFactory(), reporterFactory);
    Main cli = new Main(reporterFactory.plainTextReporter(), system);

    Command runnableCommand = null;
    try {
      runnableCommand = cliParser.parseCommand(Arrays.asList(args));
    } catch (Exception e) {
      System.err.println("run: The following options are required: [--reporter], [--spec-classpath]");
      System.exit(1);
    }

    cli.runCommand(runnableCommand);
  }

  private static ArgumentParser cliArgumentParser(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    MainParameters mainParameters = new MainParameters(commandFactory, reporterFactory);
    return new MultiCommandParser("javaspec", mainParameters)
      .addCliCommand("help", new HelpParameters(commandFactory, reporterFactory))
      .addCliCommand("run", new RunParameters(commandFactory, reporterFactory));
  }

  Main(Reporter reporter, ExitHandler system) {
    this.reporter = reporter;
    this.system = system;
  }

  void runCommand(Command command) {
    Result result = command.run();
    result.reportTo(this.reporter);
    this.system.exit(result.exitCode);
  }

  //Parses an entire command line into an executable command
  @FunctionalInterface
  interface ArgumentParser {
    Command parseCommand(List<String> arguments);
  }

  @FunctionalInterface
  interface ExitHandler {
    void exit(int statusCode);
  }
}
