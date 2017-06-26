package com.github.oxygenPlugins.common.xml.xsd;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.github.oxygenPlugins.common.text.TextSource;
import com.github.oxygenPlugins.common.xml.exceptions.ValidationSummaryException;

public interface XercesI {
	public void validateSource(TextSource source) throws IOException, SAXException, ValidationSummaryException;
	public void validateSource(File instanceFile) throws IOException,  SAXException, ValidationSummaryException;
}
