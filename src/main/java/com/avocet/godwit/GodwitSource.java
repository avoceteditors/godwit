package com.avocet.godwit;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Document;
import javax.xml.xpath.XPathExpression;

public class GodwitSource {
    public boolean valid = false;
    public File file;
    public Document doc;
    public List<File> files = new ArrayList<File>();

    public GodwitSource(){}

    public void scrubDincs(XPathExpression x){}
    public void compileStats(XPathExpression x, XPathExpression y){}
}
