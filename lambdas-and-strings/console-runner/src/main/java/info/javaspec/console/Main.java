package info.javaspec.console;

import info.javaspec.console.help.HelpParameters;
import info.javaspec.lang.lambda.RunParameters;

import java.util.Arrays;
import java.util.List;

public final class Main {
  private final Reporter reporter;
  private final ExitHandler system;

  public static void main(String... args) {
    //Just wire things up with concrete classes.  Delegate all action, to make it testable.
    ReporterFactory reporterFactory = new StaticReporterFactory(System.out);
    main(
      cliArgumentParser(new StaticCommandFactory(), reporterFactory),
      reporterFactory,
      System::exit,
      args
    );
  }

  static void main(ArgumentParser cliParser, ReporterFactory reporterFactory, ExitHandler system, String... args) {
    //Enable testing with doubles by doing all logic (even if messy) via interfaces
    Command runnableCommand = null;
    try {
      runnableCommand = cliParser.parseCommand(Arrays.asList(args));
    } catch (Exception e) {
      System.err.println("run: The following options are required: [--reporter], [--spec-classpath]");
      System.exit(1);
    }

    Main cli = new Main(reporterFactory.plainTextReporter(), system);
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
