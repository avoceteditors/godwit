package com.avocet.godwit;

// Picocli Imports 
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

// Java Imports
import java.io.File;
import java.lang.String;
import java.util.concurrent.Callable;
import java.util.List;
import java.util.logging.Logger;
import org.fusesource.jansi.AnsiConsole;

// Local Imports
import com.avocet.godwit.Kluit;

/**
 * Hello world!
 *
 */
@Command(
	name="gwt", 
	mixinStandardHelpOptions=true, 
	version="gwt 1.0", 
	description="XML Document Processor")
public class App implements Callable<Integer>{

  // Logger

  // Options
  @Option(
	names={"-b", "--build"}, defaultValue="book", paramLabel="TYPE",
	description="Type of build to run, (defaults to ${DEFAULT-VALUE}).")
  private String build_type;

  @Option(
	names={"-c", "--cache"}, defaultValue="project.xml", paramLabel="CFG",
	description="Sets the cache file, (defaults to ${DEFAULT-VALUE}).  Generated from source data.")
  private File cache;

  @Option(
	names={"-D", "--debug"},
	description="Enables debugging output in logging messages.")
  public static boolean debug;

  @Option(
	names={"-f", "--force"},
	description="Forces certain operations.")
  private boolean force;

  @Option(
	names={"-s", "--source"}, defaultValue="src", paramLabel="SRC",
	description="Sets the source directory, (defaults to ${DEFAULT-VALUE}).  Used to collect source data.") 
  private File source;

  @Option(
	names={"-v", "--verbose"}, 
	description="Enables verbosity in logging output")
  public static boolean verbose;

  // Main Process
  public static void main( String[] args ) {
	int exitCode = new CommandLine(new App()).execute(args);
	System.exit(exitCode);
  }

  /* ;;;;;;;;;;;;;;;;;;;;;; LOGGING ::::::::::::::::::::::::*/
  private static Logger logger;
  public void configLogger(){
      AnsiConsole.systemInstall();
      Kluit logConf = new Kluit();
      logConf.set(verbose, debug);

      logger = logConf.getLogger();
  }
  

   /* ;;;;;;;;;;;;;;;;;;;;;; COMMANDS ::::::::::::::::::::::::*/
  @Command(name="info")
  private int info(){
	// Initialize Logging
    configLogger();
    logger.warning("Testing");
	return 0;
  }

  @Command(name="compile",
	description="Compiles source data from configuration file")
  private int compile(){
	return 0;
  }

  @Override
  public Integer call() throws Exception {
	return 0;
  }

}
