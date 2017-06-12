package com.nkutsche.common.xml.xslt;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;

import com.nkutsche.common.text.TextSource;
import com.nkutsche.common.text.uri.DefaultURIResolver;
import com.nkutsche.common.xml.exceptions.XSLTErrorListener;

import net.sf.saxon.lib.OutputURIResolver;

public class XSLTStep {
	
	
	
//	private static TransformerFactory transfac;
	public static MyTransformerFactory transfac;
	private static XSLTErrorListener factoryErrorListener;
	
	static {
		try {
			factoryErrorListener = new XSLTErrorListener();
			transfac = new MyTransformerFactory() {
				private TransformerFactory transfac = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
				public void setErrorListener(ErrorListener errorListener) {
					transfac.setErrorListener(factoryErrorListener);
				}
				
				public Transformer newTransformer(Source xsl) throws TransformerConfigurationException {
					return transfac.newTransformer(xsl);
				}
				
				public void setOutputUriResolver(OutputURIResolver outputUriResolver){
					transfac.setAttribute("http://saxon.sf.net/feature/outputURIResolver", outputUriResolver);
				}
				
				public void setAttribute(String key, Object value) {
					transfac.setAttribute(key, value);
				}
			}; 
					
					
		} catch (Exception e){
			System.err.println(e.getMessage());
			
		}
		
	}

	private final Transformer transformer;
	
	private XSLTErrorListener listener = new XSLTErrorListener();
	private ArrayList<TextSource> secondaryResults = new ArrayList<TextSource>();
	private String sourceCode = "UNKNOWN";
	private String systemId = "UNKNOWN";
	
	
	public XSLTStep(TextSource virtualXSL, XSLTErrorListener errorListener, ArrayList<Parameter> params) throws XSLTErrorListener {
		this(SaxonUtils.getStreamSource(virtualXSL), errorListener, params);
		this.sourceCode = virtualXSL.toString();
		this.systemId = virtualXSL.getFile().toString();
	}

	public XSLTStep(File xslsheet, XSLTErrorListener errorListener, ArrayList<Parameter> params) throws XSLTErrorListener {
		this(SaxonUtils.getStreamSource(xslsheet), errorListener, params);
		this.systemId = xslsheet.toString();
	}
//	ErrorListener elt = transfac.getErrorListener();
	public XSLTStep(Source xsl, final XSLTErrorListener el, ArrayList<Parameter> params) throws XSLTErrorListener{
		this.listener = el;
		
		systemId = xsl.getSystemId();
//		try {
//			sourceCode = TextSource.readTextFile(xsl).toString();
//		} catch (IOException e2) {
//		}
		
//		transfac.setErrorListener(el);
		OutputURIResolver outputUriResolver = new OutputURIResolver() {
			
			private StringWriter writer;
			private File absFile;
			
			public Result resolve(String href, String base) throws TransformerException {
				
				URI uri = URI.create(base);
				uri = uri.resolve(href);
				absFile = new File(uri);
				
				writer = new StringWriter();
				Result result = new StreamResult(writer);
				result.setSystemId(absFile.toURI().toString());
				return result;
			}
			
			public void close(Result result) throws TransformerException {
				
				TextSource resultSource = TextSource.createVirtualTextSource(absFile);
				resultSource.setData(writer.toString());
				secondaryResults.add(resultSource);
			}
			
			public OutputURIResolver newInstance() {
				return this;
			}
		};
		try {
			transfac.setOutputUriResolver(outputUriResolver);
			
		} catch (java.lang.IllegalArgumentException e){}
		try {
			transformer = transfac.newTransformer(xsl);
		} catch (TransformerConfigurationException e) {
			try {
				listener.fatalError(e);
				listener.copyErrors(factoryErrorListener);
				factoryErrorListener.clear();
				
				throw listener;

			} catch (TransformerException e1) {
				throw factoryErrorListener;
			}
		}
		transformer.setErrorListener(el);
		if(TextSource.hasResolver()){
			DefaultURIResolver resolver = TextSource.getResolver();
			URIResolver transformerResolver = transformer.getURIResolver();
			if(transformerResolver != null){
				resolver.setFallbackURIResolver(transformer.getURIResolver());
			}
			transformer.setURIResolver(resolver);
			
		}
		setParameters(params);
	}
	public XSLTStep(Source xslStream, ArrayList<Parameter> params) throws XSLTErrorListener{
		this(xslStream, new XSLTErrorListener(), params);
	}
	
	public XSLTStep(TextSource virtualXSL, ArrayList<Parameter> params) throws XSLTErrorListener{
		this(virtualXSL, new XSLTErrorListener(), params);
	}
	
	public XSLTStep(File xslsheet, ArrayList<Parameter> params) throws XSLTErrorListener{
		this(xslsheet, new XSLTErrorListener(), params);
	}
	
	public void setOutputProperty(Properties props){
		transformer.setOutputProperties(props);
	}
	
	private void setParameters(ArrayList<Parameter> params){
		for (Iterator<Parameter> i = params.iterator(); i.hasNext();) {
			Parameter p = i.next();
			this.transformer.setParameter(p.getQName(), p.getValue());
		}
	}
	
	public void setURIResolver(URIResolver resolver){
		this.transformer.setURIResolver(resolver);
	}
	
	public TextSource transform(TextSource source, ArrayList<Parameter> params){
		setParameters(params);
		return transform(source, source.getFile());
	}
	
	public TextSource transform(TextSource source){
		return transform(source, source.getFile());
	}
	
	public TextSource transform(TextSource source, File outFile, ArrayList<Parameter> params){
		setParameters(params);
		return transform(source, outFile);
	}
	
	private TextSource transform(TextSource source, File outFile){
		StreamSource ss = new StreamSource(new StringReader(source.toString()));
		ss.setSystemId(source.getFile());
		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);
		URI baseUri = new File(".").getAbsoluteFile().toURI();
		result.setSystemId(baseUri.resolve(outFile.toURI()).toString());
		transform(ss, result);
		TextSource resultSource = TextSource.createVirtualTextSource(outFile);
		resultSource.setData(writer.toString());
		return resultSource;
	}
	
	
	private Result transform(Source source, Result result){
//		this.transformer.reset();
//		this.transformer.clearParameters();
		try {
			this.transformer.transform(source, result);
		} catch (TransformerException e) {
			try {
				this.listener.error(e);
			} catch (TransformerException e1) {
				e1.printStackTrace();
			}
		}
		transformer.reset();
		return result;
	}

	public ArrayList<TransformerException> getErrors(int level) {
		return listener.getErrors(level);
	}
	

	public ArrayList<TextSource> getSecondaryResults() {
		return new ArrayList<TextSource>(this.secondaryResults);
	}
	
	@Override
		public String toString() {
			return this.systemId + ":\n" + this.sourceCode;
		}

}
