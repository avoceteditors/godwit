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
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.io.FilenameUtils;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.FileWriter;
import java.io.IOException;

// Saxon
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;


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
    private Namespaces xmlns;
    private XPath xp;

    // XPath Targets
    public Source(File srcPath){
        logger.config("Initializing Source Data");

        // XML Tools
        this.prc = new Processor(false);
        this.bldr = prc.newDocumentBuilder();
        this.xp = XPathFactory
            .newInstance()
            .newXPath();
        Namespaces.setContext(this.xp);
    
        // Assemble Source Data
        assemble(srcPath);
        if (this.data.isEmpty()){
            logger.severe("Unable to locate XML files in --source");
            System.exit(1);
        } else {
            logger.info(String.format("Number of Source Files: %d", this.data.size()));
        }

        logger.info("Compiling to single source");

    }

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
                if (ext.equals("xml")){
                    src = new SourceData(f, 0);
                    if (src.valid){
                        this.data.put(f, src);
                    }
                } 
            } else {
                logger.warning(String.format("Path is neither file nor directory: %s", f.toString()));
            }
        }
    }

    public void compile(File cache){

        // Initialize Variables
        List<File> series = new ArrayList<File>();
        List<File> books = new ArrayList<File>();
        String tag;
        SourceData src;
        File path;

        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            if (!cache.exists()){
                logger.info("Creating cache directory");
                cache.mkdirs();
            }

            // Build Lists of Series and Books
            for (File i : this.data.keySet()){
                tag = this.data.get(i).tag;
                if (tag == "series"){
                    series.add(i);
                } else if (tag == "book"){
                    books.add(i);
                }
            }

            if (!series.isEmpty()){
                logger.info("Source contains a book:series");

                for (File i : series){
                    src = this.data.get(i);
                    this.store(cache, src, trans);
                }
            }
            else if (!books.isEmpty()){
                logger.info("Source contains a book:book");

                for (File i : books){
                    src = this.data.get(i);
                    this.store(cache, src, trans);
                }
            }
        }

        catch(TransformerConfigurationException e){
            logger.severe("Unable to initialize transformer");
            e.printStackTrace();
        }

    }

    public void store(File cache, SourceData src, Transformer trans){

        // Output Writer 
        try {
            File path = new File(cache, String.format("%s.xml", src.id));
            FileWriter fw = new FileWriter(path);

            // Transform
            trans.transform(new DOMSource(src.doc), new StreamResult(fw));
        }
        catch(TransformerException e){
            logger.warning("Unable to transform file");
            e.printStackTrace();
        }

        catch(IOException e){
            logger.warning("Unable to write file");
            e.printStackTrace();
        }
    }


    public void dinc(SourceData src){
        try {
            XPathExpression ex = this.xp.compile("//dion:include");
            NodeList nodes = (NodeList) ex.evaluate(src.doc, XPathConstants.NODESET);

            Node node;

            for(int i = 0; i < nodes.getLength(); i++){
                node = nodes.item(i);
                System.out.println(node.toString());
            }
        }
        catch(XPathExpressionException e){
            logger.warning("Error processing XPath Expression");
            e.printStackTrace();

            src.valid = false;
        }
    }


    public void build(String buildType){

        // Initialize Variables
        List<File> series = new ArrayList<File>();
        List<File> books = new ArrayList<File>();
        String tag;
        SourceData src;

        // Initialize Saxon Processor
        Processor proc = new Processor(false);
        DocumentBuilder docbldr = proc.newDocumentBuilder();
        XdmNode node;
            

        // Build Lists of Series and Books
        for (File i : this.data.keySet()){
            tag = this.data.get(i).tag;
            if (tag == "series"){
                series.add(i);
            } else if (tag == "book"){
                books.add(i);
            }
        }


        if (!series.isEmpty()){
            logger.info("Source contains a book:series");

            for (File i : series){
                src = this.data.get(i);

            }
        }
        else if (!books.isEmpty()){
            logger.info("Source contains a book:book");

            for (File i : books){
                src = this.data.get(i);
            }
        }
    }

    public void transform(SourceData src){
        logger.info("Performing Transformation");

    }
        
}
