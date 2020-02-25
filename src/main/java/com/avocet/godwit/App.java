package com.avocet.godwit;

// Module Imports
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Hello world!
 *
 */
@Command(
	name="gwt", 
	mixinStandardHelpOptions=true, 
	version="gwt 1.0", 
	description="XML document processor and renderer")
public class App {

  // Options
  @Option(
	  names = {"-v", "--verbose"}, 
	  description="Enables verbosity in logging output")
  private boolean verbose;

  // Main Process
  public static void main( String[] args ) {
	int exitCode = new CommandLine(new App()).execute(args);
	System.exit(exitCode);

  }

}
