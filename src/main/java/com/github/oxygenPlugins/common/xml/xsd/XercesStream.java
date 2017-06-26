package com.github.oxygenPlugins.common.xml.xsd;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.github.oxygenPlugins.common.text.TextSource;
import com.github.oxygenPlugins.common.xml.exceptions.ValidationSummaryException;
import com.github.oxygenPlugins.common.xml.exceptions.XSLTErrorListener;
import com.github.oxygenPlugins.common.xml.xslt.SaxonUtils;

public class XercesStream implements XercesI {

	private ArrayList<SAXParseException> errorList = new ArrayList<SAXParseException>();
	private ArrayList<SAXParseException> warningList = new ArrayList<SAXParseException>();
	
	private ErrorHandler eHandler = new ErrorHandler() {

		public void warning(SAXParseException exception) throws SAXException {
			warningList.add(exception);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			errorList.add(exception);
		}

		public void error(SAXParseException exception) throws SAXException {
			errorList.add(exception);
		}
	};
	
	private final Schema schema;
	
	public XercesStream(InputSource schemaSrc) throws SAXException {
		this(SaxonUtils.getStreamSource(schemaSrc));
	}
	
	public XercesStream(Source schemaSrc) throws SAXException {
		SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		schema = sf.newSchema(schemaSrc);
	}

	public XercesStream(InputStream streamSource) throws SAXException {
		this(SaxonUtils.getStreamSource(streamSource));
	}

	@Override
	public void validateSource(TextSource source) throws IOException, ValidationSummaryException {
		validateSource(source.createSource());
	}

	@Override
	public void validateSource(File instanceFile) throws IOException, ValidationSummaryException {
		validateSource(SaxonUtils.getStreamSource(instanceFile));
		
	}
	
	private void validateSource(Source source) throws IOException, ValidationSummaryException {
		Validator v = this.schema.newValidator();
		this.errorList = new ArrayList<SAXParseException>();
		v.setErrorHandler(eHandler);
		try {
			v.validate(source);
		} catch (SAXException e) {
			throw ValidationSummaryException.createValidationSummary("Schema error(s) in the instance " + source.getSystemId() + ":", this.errorList);
		}
		if(this.errorList.size() > 0)
			throw ValidationSummaryException.createValidationSummary("Schema error(s) in the instance " + source.getSystemId() + ":", this.errorList);
		if(this.warningList.size() > 0){
			ValidationSummaryException exc = ValidationSummaryException.createValidationSummary("Schema warnings(s) in the instance " + source.getSystemId() + ":", this.warningList);
			System.err.println(exc.getMessage());
		}
	}
	

}
