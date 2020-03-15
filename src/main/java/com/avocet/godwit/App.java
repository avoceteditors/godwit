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
import java.util.logging.ConsoleHandler;
import java.util.logging.LogManager;
import java.util.logging.Level;

// Local Imports
import com.avocet.godwit.Operation;
import com.avocet.godwit.GLogBaseFormat;
import com.avocet.godwit.GLogDebugFormat;



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
  

  // General Members
  private Operation op;
  private static final LogManager logmgr = LogManager.getLogManager();
  private static final Logger logger = logmgr.getLogger(Logger.GLOBAL_LOGGER_NAME);

  private static void setLogger(){
	logger.setUseParentHandlers(false);
	
	ConsoleHandler handler = new ConsoleHandler();

	if (debug){
	  handler.setFormatter(new GLogDebugFormat());
	} else {
	  handler.setFormatter(new GLogBaseFormat());
	}
	logger.addHandler(handler);
	logger.setLevel(Level.FINEST);
  } 


  /* ;;;;;;;;;;;;;;;;;;;;;; COMMANDS ::::::::::::::::::::::::*/
  @Command(name="info")
  private int info(){
	// Initialize Logging
	setLogger();
	logger.info("Called information operation");

	// Initialize Operation
	logger.finest("Initializing operation");
	op = new Operation();
	logger.finest("Operation initialzied");

	logger.finest("Running operation");
	op.info(cache, source);

	return 0;
  }

  @Command(name="compile",
	description="Compiles source data from configuration file")
  private int compile(){
	setLogger();
	logger.info("Called compile operation");

	// Initialize Operation
	logger.finest("Initializing operation");
	op = new Operation();
	logger.finest("Operation initialzied");

	logger.finest("Running operation");
	op.compile(cache, source);


	return 0;
  }

  @Override
  public Integer call() throws Exception {
	return 0;
  }

}
