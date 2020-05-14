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
import java.io.File;
import org.apache.commons.io.FilenameUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// XPath
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.FileWriter;

// Local Imports
import com.avocet.godwit.Kluit;
import com.avocet.godwit.Namespaces;

public class Compiler {

    private static final Logger logger = Kluit.getLogger(Compiler.class.getName());
    public File file;

    // XML Document Data
    private DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
    private DocumentBuilder bldr;
    public Document doc;

    // XPath Document Data
    private XPathFactory xpfact = XPathFactory.newInstance();
    private XPath xpath;
    private Namespaces ns = new Namespaces();

    // Constructors
    public Compiler(File src, boolean validate){
        logger.info("Initializing compiler");
        this.file = src.getAbsoluteFile();

        // Initialize DocumentBuilder
        try {
            this.fact.setNamespaceAware(true);
            this.fact.setXIncludeAware(true);
            this.fact.setValidating(validate);
            this.bldr = this.fact.newDocumentBuilder();
            this.xpath = this.xpfact.newXPath();
            this.ns.setContext(this.xpath);

            // Assemble files
            if (src.exists()){
                assemble(this.file);
            }
            else {
                logger.severe("Source path does not exist");
                System.exit(1);
            }
        }
        catch(ParserConfigurationException e){
            logger.severe("Error building document");
            e.printStackTrace();
        }

    }

    public void save(File cache){
        // Make Absolute

        // Validate
        if(!cache.exists()){
            cache.mkdirs();
        } else if (cache.isFile()){
            logger.severe(String.format("Warning, cache directory %s is a file", cache.toString()));
            System.exit(1);
        }
        cache = new File(cache, this.file.getName()).getAbsoluteFile(); 

        try {
            // Initialize XSLT Cache Transformer 
            Transformer trans = TransformerFactory
                .newInstance()
                .newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            // Write to File
            FileWriter fw = new FileWriter(cache);
            trans.transform(new DOMSource(this.doc), new StreamResult(fw));

        } catch(TransformerException e){
            logger.severe("Unable to save cache to file");
            System.exit(1);
        } catch(IOException e){
            logger.severe("I/O exception blocks cache transformer");
            System.exit(1);
        }
        
    }

    /**
     * Recursively assemble file list
     */
    public void assemble(File file){
        logger.info("Assembling project");
        try {
            // Load Document
            if (FilenameUtils.getExtension(file.toString()).equals("xml")){
                this.doc = this.bldr.parse(file);
            } else {
                logger.severe(String.format("Unrecognized project file type: %s", file.toString()));
                System.exit(1);
            }
        } catch(SAXException e){
            logger.severe("Error assembling project");
            e.printStackTrace();
            System.exit(1);
        } catch(IOException e){
            logger.severe("Error reading project file");
            e.printStackTrace();
            System.exit(1);
        } catch(IllegalArgumentException e){
            logger.severe("Invalid arguments");
            e.printStackTrace();
        }

    }
}
