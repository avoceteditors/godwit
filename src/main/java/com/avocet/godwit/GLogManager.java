/*
 * Copyright (c) 2017, Kenneth P. J. Dyer <kenneth@avoceteditors.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the copyright holder nor the name of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.avocet.godwit;

import java.lang.String;
import java.lang.Boolean;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;

import com.avocet.godwit.GLogBaseFormat;
import com.avocet.godwit.GLogDebugFormat;
import java.util.Properties;

public final class GLogManager {

  private GLogManager(){}

  public static void set(boolean debugArg, boolean verboseArg){
	String verbose = Boolean.toString(verboseArg);
	String debug = Boolean.toString(debugArg);

	System.setProperty("godwit.verbose", verbose);
	System.setProperty("godwit.debug", debug);
  }

  public static Logger getLogger(){
	return getLogger("GLOBAL");
  }

  public static Logger getLogger(String name){
	// Properties
	boolean debug = Boolean.getBoolean("godwit.debug");
	boolean verbose = Boolean.getBoolean("godwit.verbose");

	// Init Handler
	ConsoleHandler handler = new ConsoleHandler();
	if (debug) {
	  handler.setFormatter(new GLogDebugFormat());
	} else {
	  handler.setFormatter(new GLogBaseFormat());
	}

	// Init Logger
	Logger logger = Logger.getLogger(name);
	logger.setUseParentHandlers(false);
	logger.addHandler(handler);

	// Set Handler
	if (debug & verbose){
	  logger.setLevel(Level.FINEST);
	}
	else if (debug){
	  logger.setLevel(Level.FINE);
	}
	else if (verbose){
	  logger.setLevel(Level.INFO);
	}
	else {
	  logger.setLevel(Level.WARNING);
	}

	// Return Logger
	return logger;
  }
}

  

