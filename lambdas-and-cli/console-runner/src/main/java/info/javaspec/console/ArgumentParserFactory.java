package info.javaspec.console;

import info.javaspec.console.help.HelpParameters;
import info.javaspec.lang.lambda.RunParameters;

class ArgumentParserFactory {
  public static Main.ArgumentParser forConsole(CommandFactory commandFactory, ReporterFactory reporterFactory) {
    MainParameters mainParameters = new MainParameters(commandFactory, reporterFactory);
    return new MultiCommandParser("javaspec", mainParameters)
      .addCliCommand("help", new HelpParameters(commandFactory, reporterFactory))
      .addCliCommand("run", new RunParameters(commandFactory, reporterFactory));
  }
}
