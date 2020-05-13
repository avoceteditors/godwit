
<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="3.0"
	xmlns:book="http://docbook.org/ns/docbook"
	xmlns:xi="http://www.w3.org/2001/XInclude"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:dion="http://avoceteditors.com/xml/dion">

<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

<xsl:template match="//book:book">
<xsl:variable name="idref"><xsl:value-of select="@xml:id"/></xsl:variable>
<xsl:result-document href="output/latex/{$idref}.tex">
    Test
</xsl:result-document>
</xsl:template>

</xsl:stylesheet>

