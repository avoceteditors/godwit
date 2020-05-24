package com.avocet.godwit;

import com.avocet.godwit.GodwitSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;
import java.io.IOException;

import javax.xml.xpath.XPathExpression;
import java.util.logging.Logger;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import java.io.FileFilter;
import org.apache.commons.io.FilenameUtils;
import com.avocet.godwit.Namespaces;
import java.util.Date;
import java.util.Calendar;

public class XMLSource extends GodwitSource {

    private static final Logger logger = Logger.getLogger(XMLSource.class.getName());
    public File file;
    private XPathExpression findText;

    public XMLSource(File f, DocumentBuilder bldr, XPathExpression findText){
        logger.config(String.format("Reading XML file: %s", f.toString()));
        this.file = f;
        this.findText = findText;
        try {
            this.doc = bldr.parse(this.file);
            this.valid = true;
            this.setAttrs();
        }
        catch(SAXException e){
            logger.warning("Issue parsing XML Document");
            logger.config(e.getStackTrace().toString());
            this.valid = false;
        }
        catch(IOException e){
            logger.warning("Issue reading file");
            logger.config(e.getStackTrace().toString());
            this.valid = false;
        }
    }

    public void setAttrs(){
        logger.config("Recording element attributes");
        Element element = this.doc.getDocumentElement();

        // Set Path
        element.setAttributeNS(Namespaces.dion, "dion:path", this.file.toString());

        // Mtime
        long mtime = this.file.lastModified();
        Date date = new Date(mtime);

        element.setAttributeNS(Namespaces.dion, "dion:mtime", String.valueOf(mtime));
        element.setAttributeNS(Namespaces.dion, "dion:mtime_readable", date.toString()); 

        // TODO: Date Complexity YYYY-MM-DD, (C) YYYY, MM DD, YYYY, etc.
        
        // TODO: Collect file statistics.
        logger.config("Collecting document statistics");
        try {
            NodeList nodes = (NodeList) this.findText.evaluate(this.doc, XPathConstants.NODESET);
            String text;
            String[] wordarray;
            int lines = 0;
            int words = 0;
            int chars = 0;
            int j;

            for (int i = 0; i < nodes.getLength(); i++){
                // Line Count
                lines += 1;

                // Word Count
                text = nodes.item(i).getNodeValue();
                wordarray = text.trim().split("\\s+");
                words += wordarray.length;
                chars += text.length();
            }
            element.setAttributeNS(Namespaces.dion, "dion:file_lc", String.valueOf(lines));
            element.setAttributeNS(Namespaces.dion, "dion:file_wc", String.valueOf(words));
            element.setAttributeNS(Namespaces.dion, "dion:file_cc", String.valueOf(chars));
            
        } catch(XPathExpressionException e){
            logger.warning("Unable to collect document statistics");
            logger.config(e.getStackTrace().toString());
            element.setAttributeNS(Namespaces.dion, "dion:file_lc", String.valueOf(0));
            element.setAttributeNS(Namespaces.dion, "dion:file_wc", String.valueOf(0));
            element.setAttributeNS(Namespaces.dion, "dion:file_cc", String.valueOf(0));
        }
        
    }

    public void scrubDincs(XPathExpression findDinc){
        logger.info("Scrubbing dion:includes");
        Node dinc, parent, child;
        NodeList children;
        try {
            NodeList nodes = (NodeList) findDinc.evaluate(this.doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++){
                dinc = nodes.item(i);
                parent = dinc.getParentNode();
                children = dinc.getChildNodes();
                for (int j = 0; j < children.getLength(); j++){
                    child = children.item(j);
                    parent.insertBefore(child, dinc);
                }
                parent.removeChild(dinc);
            }
        }
        catch(XPathExpressionException e){
            logger.warning("Error scrubbing dion:includes");
            logger.config(e.getStackTrace().toString());
        }
    }

    public void findIncludes(XPathExpression findDinc){
        logger.config("Reading for dion:includes");
        try {

            // Initialize Variables
            Node node, source, parent;
            File filePath;
            FilenameFilter filter;
            NamedNodeMap map;
            GodwitSource src;

            // Loop Dincs
            NodeList nodes = (NodeList) findDinc.evaluate(this.doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++){
                node = nodes.item(i);
                map = node.getAttributes();
                source = map.getNamedItem("src");
                parent = map.getNamedItem("parent");
                if (source != null){
                    if (parent == null){
                        filePath = new File(this.file.getParent(), source.getNodeValue());
                        if (filePath.exists()){
                            this.files.add(filePath);
                        } 
                    } else {
                        filter = new WildcardFileFilter(source.getNodeValue());
                        this.files = Arrays.asList(new File(parent.getNodeValue()).listFiles(filter));
                    }
                    logger.config(String.format("%d files found in dion:include: %s", files.size(), this.file.toString()));
                }
            }
        }
        catch(XPathExpressionException e){
            logger.warning("Issue running XPath for dion:includes");
            logger.config(e.getStackTrace().toString());
        }
    }

    public void compileStats(XPathExpression findBookChap, XPathExpression findDpath){
        logger.info("Compiling document statistics");
        try {
            NodeList files;
            NodeList sects = (NodeList) findBookChap.evaluate(this.doc, XPathConstants.NODESET);
            Element sect, file; 
            int lc,wc,cc; 
            String lcs, wcs, ccs;
            List<String> lcl = new ArrayList<String>();
            List<String> wcl = new ArrayList<String>();
            List<String> ccl = new ArrayList<String>();

            for (int i = 0; i < sects.getLength(); i++){
                // Init Variables
                sect = (Element) sects.item(i);
                lc = 0;
                wc = 0;
                cc = 0;

                // Retrieve Parent File Data
                lcl.add(sect.getAttributeNS(Namespaces.dion, "file_lc"));
                wcl.add(sect.getAttributeNS(Namespaces.dion, "file_wc"));
                ccl.add(sect.getAttributeNS(Namespaces.dion, "file_cc"));

                // Collect Subsections
                files = (NodeList) findDpath.evaluate(sect, XPathConstants.NODESET);
                for (int j = 0; j < files.getLength(); j++){
                    file = (Element) files.item(j);
                    lcl.add(file.getAttributeNS(Namespaces.dion, "file_lc"));
                    wcl.add(file.getAttributeNS(Namespaces.dion, "file_wc"));
                    ccl.add(file.getAttributeNS(Namespaces.dion, "file_cc"));
                }

                // Build Line Count
                for (int j = 0; j < lcl.size(); j++){
                    lcs = lcl.get(j);
                    wcs = wcl.get(j);
                    ccs = ccl.get(j);

                    // Add Lines
                    if (!lcs.equals("")){
                        lc += Integer.valueOf(lcs);
                    }
                    if (!wcs.equals("")){
                        wc += Integer.valueOf(wcs);
                    }
                    if (!ccs.equals("")){
                        cc += Integer.valueOf(ccs);
                    }
                    
                }

                // Save Stats
                sect.setAttributeNS(Namespaces.dion, "dion:lc", String.valueOf(lc));
                sect.setAttributeNS(Namespaces.dion, "dion:wc", String.valueOf(wc));
                sect.setAttributeNS(Namespaces.dion, "dion:cc", String.valueOf(cc));
            }
        }
        catch(XPathExpressionException e){
            logger.warning("Issue finding books/chapters or @dion:paths to compile statistical attributes");
            logger.config(e.getStackTrace().toString());
        }
    }
}
