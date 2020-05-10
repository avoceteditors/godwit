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

// Module Imports
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

/**
 * LogManager alernative, used to set a default console logger
 */
public class Kluit {
    private static boolean verbose;
    private static boolean debug;

    public void set(boolean verboseArg, boolean debugArg){
        Kluit.verbose = verboseArg;
        Kluit.debug = debugArg;
    }

    public static Logger getLogger(String name){

        // Initialize Logger
        Logger logger = Logger.getLogger(name);

        // Disable Parent Handlers
        logger.setUseParentHandlers(false);

        // Initialize Handler
        ConsoleHandler handler = new ConsoleHandler();

        // Set Logging Level
        if (debug & verbose){
            logger.setLevel(Level.FINEST);

        } 
        else if (debug){
            logger.setLevel(Level.CONFIG);
        }
        else if (verbose){
            logger.setLevel(Level.INFO);
        }
        else {
            logger.setLevel(Level.WARNING);
        }

        // Configure Formatter
        handler.setFormatter(new SimpleFormatter(){

            @Override
            public synchronized String format(LogRecord lr){
                String msg;
                Level lvl = lr.getLevel();

                // Set Debugging Message
                if (Kluit.debug){
                    msg = String.format("[ %s:%s.%s() ]: %s\n", 
                            lvl.getLocalizedName(),
                            lr.getSourceClassName(),
                            lr.getSourceMethodName(),
                            lr.getMessage());
                } 
                
                // Set Standard Message
                else {
                    msg = String.format("[ %s ]: %s\n",
                            lvl.getLocalizedName(),
                            lr.getMessage());
                }

                // Color Log Message and Return
                if (lvl == Level.CONFIG){
                    return ansi().fg(BLUE).a(msg).reset().toString();
                }
                else if (lvl == Level.INFO){
                    return ansi().fg(GREEN).a(msg).reset().toString();
                }
                else if(lvl == Level.WARNING){
                    return ansi().fg(YELLOW).a(msg).reset().toString();
                } 
                else if(lvl == Level.SEVERE){
                    return ansi().fg(RED).a(msg).reset().toString();
                }
                else {
                    return msg;
                }
            }
        });
        logger.addHandler(handler);

        return logger;
    }


    public static Logger getLogger(){
        return getLogger("DEFAULT");
    }

}
