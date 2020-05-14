// Package
package com.avocet.godwit;

// Module Imports
import javax.xml.xpath.XPath;
import javax.xml.namespace.NamespaceContext;
import javax.xml.XMLConstants;
import java.lang.NullPointerException;
import java.lang.UnsupportedOperationException;
import java.util.Map;
import static java.util.Map.entry;
import java.util.Iterator;

public class Namespaces {

    public static final String book = "http://docbook.org/ns/docbook";
    public static final String xi = "http://www.w3.org/2001/XInclude";
    public static final String xsl = "http://www.w3.org/1999/XSL/Transform";
    public static final String xlink = "http://www.w3.org/1999/xlink";
    public static final String fo = "http://www.w3.org/1999/XSL/Format";

    public static final String dion = "http://avoceteditors.com/xml/dion";

    public static final Map<String, String> data = Map.ofEntries(
        entry("book", "http://docbook.org/ns/docbook"),
        entry("xi", "http://www.w3.org/2001/XInclude"),
        entry("xsl","http://www.w3.org/1999/XSL/Transform"),
        entry("xlink", "http://www.w3.org/1999/xlink"),
        entry("fo", "http://www.w3.org/1999/XSL/Format"),
        entry("dion", "http://avoceteditors.com/xml/dion"),
        entry("xml", XMLConstants.XML_NS_URI)
    );

    public static void setContext(XPath xpath){
        xpath.setNamespaceContext(new NamespaceContext() {
            public String getNamespaceURI(String prefix){
                if (prefix == null) throw new NullPointerException("Null XML Namespace Prefix found in Godwit");
                else if (data.keySet().contains(prefix)) return data.get(prefix);
                return XMLConstants.NULL_NS_URI;
            }

            public String getPrefix(String uri){
                throw new UnsupportedOperationException();
            }

            public Iterator getPrefixes(String uri) {
                throw new UnsupportedOperationException();
            }
        });
    }

}        
