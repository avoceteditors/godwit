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
package com.avocet.godwit.source;

// Module Imports
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

// Saxon
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;

// Local Imports
import com.avocet.godwit.Kluit;
import com.avocet.godwit.source.Namespaces;
import com.avocet.godwit.source.SourceData;

public class Source {

    // Configure Logger
    private static final Logger logger = Kluit.getLogger(Source.class.getName());

    // Map of Source Files
    public HashMap<File, SourceData> data = new HashMap<File, SourceData>();

    // XML Tools
    private DocumentBuilder bldr;
    private Processor prc;
    private XPathCompiler xpc;
    private Namespaces xmlns;

    // XPath Targets
    private XPathExecutable xpdinc;

    public Source(File srcPath){
        logger.config("Initializing Source Data");

        // XML Tools
        this.prc = new Processor(false);
        this.bldr = prc.newDocumentBuilder();

        // XPath Compiler
        this.xpc = prc.newXPathCompiler();
        this.xmlns = new Namespaces();
        this.xmlns.configureXpath(this.xpc);
        
        // Compile Common Searches
        try {
            this.xpdinc = this.xpc.compile("//dion:include");
        }
        catch(SaxonApiException e){
            logger.warning("Error compiling XPath Executable");
            logger.info(String.format("%s", e.getStackTrace()));
                
        }

        // Assemble Source Data
        assemble(srcPath);
        if (this.data.isEmpty()){
            logger.severe("Unable to locate XML files in --source");
            System.exit(1);
        }
    }

    static String correctExt = "xml";

    public void assemble(File srcDir){

        // Initialize Variables
        SourceData src;
        String ext;

        // Iterate over Files in Directory
        for (File f : srcDir.listFiles()){

            // Recurse Directory Contents
            if (f.isDirectory()){
                logger.finest("Path is directroy");
                assemble(f);
            } else if (f.isFile()){
                logger.finest("Path is file");

                // Find XML Files
                ext = FilenameUtils.getExtension(f.toString());
                f = f.getAbsoluteFile();
                if (ext.equals(correctExt)){
                    src = new SourceData(f, this.bldr);
                    if (src.valid){
                        this.data.put(f, src);
                    }
                } 
            } else {
                logger.warning(String.format("Path is neither file nor directory: %s", f.toString()));
            }
        }
    }
}
