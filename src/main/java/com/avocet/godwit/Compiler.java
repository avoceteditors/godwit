package com.avocet.godwit;

// Module Imports
import java.io.File;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

// XML
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;


// XPath
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathConstants;

// XSLT
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;

// Local
import com.avocet.godwit.GodwitSource;
import com.avocet.godwit.XMLSource;


public class Compiler {

    // Logger Configuration
    private static final Logger logger = Logger.getLogger(Compiler.class.getName());

    // General Attributes
    private File file;

    // XMl Document Attributes
    private static DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder bldr;
    private static XPathFactory xpfact = XPathFactory.newInstance();
    private static final Namespaces ns = new Namespaces();
    private static XPath xpath;
    private static XPathExpression findDinc, findText, findBookChap, findDpath;

    public Document doc;

    public Compiler(File src, boolean validate){
        logger.info("Initializing Compiler");
        this.file = src.getAbsoluteFile();

        if (this.file.exists()){

            logger.info("Configuring XML Document Factory");
            this.fact.setNamespaceAware(true);
            this.fact.setXIncludeAware(true);
            this.fact.setValidating(validate);
            this.xpath = this.xpfact.newXPath();
            this.ns.setContext(this.xpath);

            try {
                logger.info("Instantiating Document Builder");
                this.bldr = this.fact.newDocumentBuilder();
            }
            catch (ParserConfigurationException e){
                logger.severe("Unable to instantiate Document Builder");
                logger.config(e.getStackTrace().toString());
                System.exit(1);
            }

            // Configure XPath for Dincs
            try {
                logger.config("Configuring XPath Expression for dion:includes");
                this.findDinc = xpath.compile("//dion:include"); 
                this.findText = xpath.compile("//book:para//text()");
                this.findBookChap = xpath.compile("//book:book|//book:chapter");
                this.findDpath = xpath.compile(".//*[@dion:path]");
            }
            catch(XPathExpressionException e){
                logger.severe("Unable to compile XPath Expression");
                logger.config(e.getStackTrace().toString());
                System.exit(1);
            }

            // Assemble XML Data
            GodwitSource data = assemble(this.file);
            if (data.valid){
                data.scrubDincs(this.findDinc);
                data.compileStats(this.findBookChap, this.findDpath);
                this.doc = data.doc;
            } else {
                logger.severe("Unable to compile source data");
                System.exit(1);
            }
        } else {
            logger.severe(String.format("Source file does not exist: %s", this.file.toString()));
            System.exit(1);
        }
    }

    // Assemble
    public GodwitSource assemble(File f){
        logger.config(String.format("Assembling File: %s", f.toString()));
        GodwitSource src = new GodwitSource();
        GodwitSource dincSrc;
        NodeList nodes;
        Node node, source, parent, clone;
        NamedNodeMap map;
        File dincPath;
        Node element;

        if (f.exists()){
            // NOTE: Will need to refactor this block upon shift to RST.
            if(FilenameUtils.getExtension(f.toString()).equals("xml")){
                src = new XMLSource(f, this.bldr, this.findText);
                try { 
                    nodes = (NodeList) this.findDinc.evaluate(src.doc, XPathConstants.NODESET);

                    for (int i = 0; i < nodes.getLength(); i++){
                        node = nodes.item(i);
                        map = node.getAttributes();
                        source = map.getNamedItem("src");
                        parent = map.getNamedItem("parent");

                        if (source != null){
                            if (parent == null){
                                dincPath = new File(f.getParent(), source.getNodeValue());
                                if (dincPath.exists()){
                                    dincSrc = assemble(dincPath);
                                    if (dincSrc.valid){
                                        // Retrieve Node
                                        element = src.doc.importNode(dincSrc.doc.getDocumentElement(), true);
                                        node.appendChild(element);
                                    }
                                } else {
                                    logger.warning(String.format("Reference to nonexistent file found in %s with dion:include pointing to %s", f.toString(), dincPath.toString()));
                                }
                            } else {
                                //filter = new WildcardFileFilter(source.getNodeValue());
                            }
                        } else {
                            logger.warning(String.format("dion:include contains NULL src value: %s", f.toString()));
                        }
                    }

                } catch(XPathExpressionException e){
                    logger.warning("Error processing XPath for dion:includes");
                    logger.config(String.format("Error occured in %s\n%s", f.toString(), e.getStackTrace().toString()));
                }

            }
        } else {
            System.exit(1);
        }

        return src;
    }

    public void save(File cache){

        if(!cache.exists()){
            cache.mkdirs();
        } else if (cache.isFile()){
            logger.severe(String.format("Warning, cache directory %s is a file", cache.toString()));
            System.exit(1);
        }

        cache = new File(cache, this.file.getName()).getAbsoluteFile();

        try {
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            DOMSource dom = new DOMSource(this.doc);

            StreamResult file = new StreamResult(cache);
            trans.transform(dom, file);
        } 
        catch(TransformerConfigurationException e){
            logger.severe("Issue encountered in transformer configuration while saving cache");
            logger.config(e.getStackTrace().toString());
        }
        catch(TransformerException e){
            logger.severe("Issue encountered in transformer while saving cache");
            logger.config(e.getStackTrace().toString());
        }


    }

}
