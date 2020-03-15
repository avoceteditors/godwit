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
// Package
package com.avocet.godwit;

// Module Imports
import java.io.File;
import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

// Logging Imports
import com.avocet.godwit.GLogger;
import java.util.logging.Logger;
import java.util.logging.LogManager;

public class SourceDirectory {

  // Logger
  private static final Logger logger = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME) ;


  // File Variables
  private static File path = new File("src");
  private static List<File> files = new ArrayList<>(); 

  public SourceDirectory(File source){
	path = source;

	logger.config("Validating source directory");
	if (!path.exists()){
	  logger.severe(String.format("Source path does not exist: %s", path.toString()));
	  System.exit(1);
	} else if (!path.isDirectory()){
	  logger.severe(String.format("Source path is not a directory: %s", path.toString()));
	  System.exit(1);
	}
	logger.fine("Reading file paths into memory");
	read_files(path);
	logger.info(String.format("Found %d files", files.size()));
  }

  public void read_files(File argPath) {

	// Loop over Files
	File[] data = argPath.listFiles();
	File f;

	for (int i = 0; i < data.length; i++){
	  f = data[i];
	  if(f.isDirectory()){
		read_files(f);
	  } else {
		files.add(f);
	  }
	}
  }

}
