// Package
package com.avocet.godwit.source;

// Module Imports
import java.util.HashMap;
import net.sf.saxon.s9api.XPathCompiler;

public class Namespaces {


        public String[] data[] = {
            {"book", "http://docbook.org/ns/docbook"},
            {"xi", "http://www.w3.org/2001/XInclude"},
            {"xsl", "http://www.w3.org/1999/XSL/Transform"},
            {"xlink", "http://www.w3.org/1999/xlink"},
            {"fo", "http://www.w3.org/1999/XSL/Format"},
            {"dion", "http://avoceteditors.com/xml/dion"}
        };

        public Namespaces(){}

        public void configureXpath(XPathCompiler xpc){
            for(String[] xmlns : data){
                xpc.declareNamespace(xmlns[0], xmlns[1]);
            }
        }
}
