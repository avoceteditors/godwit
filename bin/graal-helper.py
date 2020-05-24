#!/usr/bin/env python3
"""
Small helper script to initialize `reflection_config.json`
"""
import json

classes = [
    "org.apache.xerces.parsers.XIncludeAwareParserConfiguration",
    "org.apache.xml.dtm.ObjectFactory",
    "org.apache.xerces.impl.dv.ObjectFactory",
    "org.apache.xerces.impl.dv.dtd.DTDDVFactoryImpl",
    "org.apache.xerces.xinclude.ObjectFactory",
    "org.apache.xerces.parsers.XIncludeParserConfiguration",
    "net.sf.saxon.Configuration",
    "javax.xml.transform.dom.DOMSource",
    "org.apache.xml.serializer.OutputPropertiesFactory",
    "org.apache.xml.serializer.utils.SerializerMessages"
]

data = [] 
for i in classes:
    data.append(
        {
            "name": i,
            "allDeclaredConstructors": True,
            "allPublicConstructors": True,
            "allDeclaredMethods": True,
            "allPublicMethods": True
            })

with open("reflection_config.json", "w") as f:
    json.dump(data, f)
