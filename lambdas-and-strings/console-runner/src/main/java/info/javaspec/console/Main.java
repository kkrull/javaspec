package info.javaspec.console;

import info.javaspec.console.help.HelpArguments;
import info.javaspec.console.help.MainArguments;
import info.javaspec.lang.lambda.RunArguments;

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
    cli.runCommand(cliParser.parseCommand(Arrays.asList(args)));
  }

  private static ArgumentParser cliArgumentParser(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    MainArguments mainParameters = new MainArguments(commandFactory, reporterFactory);
    return new MultiCommandParser(mainParameters)
      .addCliCommand("help", new HelpArguments(commandFactory, reporterFactory))
      .addCliCommand("run", new RunArguments(commandFactory, reporterFactory));
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
