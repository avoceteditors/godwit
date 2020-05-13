package com.avocet.godwit.source;

// Java Imports
import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.lang.Exception;

// Apache Commons Imports
import org.apache.commons.io.FilenameUtils;

// Saxon
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import org.xml.sax.SAXException;

// Logging
import com.avocet.godwit.Kluit;
import java.util.logging.Logger;


// Local Imports
import com.avocet.godwit.source.Namespaces;

public class SourceData{

    // Logger Configuration
    public static final Logger logger = Kluit.getLogger(SourceData.class.getName());

    // Private Attributes
    private File file;
    private long mtime;
    private static final DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder bldr;
    public Document doc;
    public String tag;
    public String id;

    /**
     * File Types:
     * - 0: XML
     */
    private int fileType = 0;

    // Public Attributes
    public boolean valid = true;

    public SourceData(File f, int typ){
        this.logger.finer(String.format("Initializing Source File; %s", f.toString()));
        this.file = f.getAbsoluteFile();
        this.mtime = this.file.lastModified();
        this.fileType = typ;

        try{
            fact.setNamespaceAware(true);
            fact.setValidating(false);
            fact.setXIncludeAware(true);
            SourceData.bldr = fact.newDocumentBuilder();
            this.read();
        }
        catch(ParserConfigurationException e){
            logger.warning("Error compiling Document Builder");
            this.valid = false;
        }

    }
    public void read(){
        if(this.fileType == 0){
            this.read_xml();
        }
    }

    public void read_xml(){
        try {
            this.doc = SourceData.bldr.parse(this.file);
            this.tag = this.doc.getDocumentElement().getTagName();
            this.id = this.doc.getDocumentElement().getAttributeNodeNS(Namespaces.data.get("xml"), "id").getValue();

            // Load Metadata
            this.doc.getDocumentElement().setAttributeNS(Namespaces.dion, "mtime", String.format("%d", this.mtime));
            this.doc.getDocumentElement().setAttributeNS(Namespaces.dion, "path", this.file.toString());


        }
        catch(IOException e){
            logger.warning("I/O Exception occurred");
            e.printStackTrace();
        }
        catch(SAXException e){
            logger.warning("XML parsing exception occurred");
            e.printStackTrace();
        }
        catch(IllegalArgumentException e){
            logger.warning("Illegal argument exception occurred");
            e.printStackTrace();
        }
    }

}
