package com.github.oxygenPlugins.common.xml.xslt;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;

import net.sf.saxon.lib.OutputURIResolver;

public interface MyTransformerFactory {
	void setErrorListener(ErrorListener errorListener);
	Transformer newTransformer(Source xsl) throws TransformerConfigurationException;
	void setAttribute(String key, Object value);
	void setOutputUriResolver(OutputURIResolver outputUriResolver);
}
