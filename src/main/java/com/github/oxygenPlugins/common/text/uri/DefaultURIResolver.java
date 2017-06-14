package com.github.oxygenPlugins.common.text.uri;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;

public class DefaultURIResolver implements URIResolver {
	
	
	private URIResolver fallbackUriResolver = null;
	
	protected DefaultURIResolver(){
		try {
			this.fallbackUriResolver = TransformerFactory.newInstance().newTransformer().getURIResolver();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		}
	}
	
	public Source resolve(String href, String base) throws TransformerException {
		if(fallbackUriResolver != null){
			return fallbackUriResolver.resolve(href, base);
		}
		throw new TransformerException("The URI " + href + " could not resolved on the base " + base + " and no default resolver was defined.");
	}
	
	public void setFallbackURIResolver(URIResolver resolver){
		this.fallbackUriResolver = resolver;
	} 

}
