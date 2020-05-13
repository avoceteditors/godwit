// Package
package com.avocet.godwit;


// Module Imports
import com.avocet.godwit.source.SourceData;
import com.avocet.godwit.source.Source;
import java.util.List;
import java.io.File;
import javax.xml.transform.stream.StreamSource;

// Saxon
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;

// Logging Configuration
import java.util.logging.Logger;
import com.avocet.godwit.Kluit;

public class Renderer {

    public static final Logger logger = Kluit.getLogger(Renderer.class.getName());

    public Renderer(Source src, Source cache, String build_type, File output){
        logger.info("Initializing renderer");
        try {

            // Initialize Saxon Process
            Processor prc = new Processor(false);
            DocumentBuilder db = prc.newDocumentBuilder();
            List<File> files;
            XsltCompiler xsltComp = prc.newXsltCompiler();
            XsltExecutable transform;
            XdmNode doc;


            if (build_type.equals("book")){
                files = cache.build();

                // Set Output
                File loutput = new File(output, "latex");
                if (!loutput.exists()){
                    logger.info("Creating output directory");
                    loutput.mkdirs();
                }
                // FIXME Load XML File from directory
                logger.warning(str.toString());
                //transform = xsltComp.compile(str);
                
                for (File f : files){
                    logger.info(String.format("Preparing document from: %s", f.toString()));
                    doc = db.build(f);

                }
            }
        }

        catch(SaxonApiException e){
            logger.severe("Error rendering documents");
            e.printStackTrace();
        }
    }
}
