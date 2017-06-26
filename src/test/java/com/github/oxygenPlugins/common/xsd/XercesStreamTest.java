package com.github.oxygenPlugins.common.xsd;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.github.oxygenPlugins.common.xml.exceptions.ValidationSummaryException;
import com.github.oxygenPlugins.common.xml.xsd.Xerces;
import com.github.oxygenPlugins.common.xml.xsd.XercesI;
import com.github.oxygenPlugins.common.xml.xsd.XercesStream;

public class XercesStreamTest {

	@Test
	public void test1SimpleXSDasInputStream() {//com/github/oxygenPlugins/common/xsd/
		
//		valid xml:
		XercesI xerc = createXerces("/com/github/oxygenPlugins/common/xsd/Test1-simple.xsd");
		File inst = new File(STRING_MAIN_FOLDER + "Test1-simple-valid.xml");
		try {
			xerc.validateSource(inst);
		} catch (IOException | SAXException | ValidationSummaryException e) {
			fail(e.getMessage());
		}
		
//		invalid xml:
		inst = new File(STRING_MAIN_FOLDER + "Test1-simple-invalid.xml");
		boolean isValid = true;
		try {
			xerc.validateSource(inst);
		} catch (IOException | SAXException | ValidationSummaryException e) {
//			instance was invalid
			isValid = false;
		}
		
		if(isValid){
			fail("The instance " + inst.getName() +" should be invalid!");
		}
		
	}
	
	@Test
	public void test1SimpleXSDasFile() {//com/github/oxygenPlugins/common/xsd/
//		create Xerces, using File
		XercesI xerc = createXerces(new File(STRING_MAIN_FOLDER + "Test1-simple.xsd"));
		
//		valid xml:
		File inst = new File(STRING_MAIN_FOLDER + "Test1-simple-valid.xml");
		try {
			xerc.validateSource(inst);
		} catch (IOException | SAXException | ValidationSummaryException e) {
			fail(e.getMessage());
		}
		
//		invalid xml:
		inst = new File(STRING_MAIN_FOLDER + "Test1-simple-invalid.xml");
		boolean isValid = true;
		try {
			xerc.validateSource(inst);
		} catch (IOException | SAXException | ValidationSummaryException e) {
//			instance was invalid
			isValid = false;
		}
		
		if(isValid){
			fail("The instance " + inst.getName() +" should be invalid!");
		}
		
	}
	
	@Test
	public void test2ComplexXSDasInputStream() {//com/github/oxygenPlugins/common/xsd/
		
//		valid xml:
		XercesI xerc = createXerces("/com/github/oxygenPlugins/common/xsd/Test2-complex.xsd");
		File inst = new File(STRING_MAIN_FOLDER + "Test2-complex-valid.xml");
		try {
			xerc.validateSource(inst);
		} catch (IOException | SAXException | ValidationSummaryException e) {
			fail(e.getMessage());
		}
		
//		invalid xml:
		inst = new File(STRING_MAIN_FOLDER + "Test2-complex-invalid.xml");
		boolean isValid = true;
		try {
			xerc.validateSource(inst);
		} catch (IOException | SAXException | ValidationSummaryException e) {
//			instance was invalid
			isValid = false;
		}
		
		if(isValid){
			fail("The instance " + inst.getName() +" should be invalid!");
		}
		
	}
	
	
	
	@Test
	public void test2ComplexXSDasFile() {//com/github/oxygenPlugins/common/xsd/
		
//		valid xml:
		XercesI xerc = createXerces(new File(STRING_MAIN_FOLDER + "Test2-complex.xsd"), XSM_NAMESPACE);
		File inst = new File(STRING_MAIN_FOLDER + "Test2-complex-valid.xml");
		try {
			xerc.validateSource(inst);
		} catch (IOException | SAXException | ValidationSummaryException e) {
			fail(e.getMessage());
		}
		
//		invalid xml:
		inst = new File(STRING_MAIN_FOLDER + "Test2-complex-invalid.xml");
		boolean isValid = true;
		try {
			xerc.validateSource(inst);
		} catch (IOException | SAXException | ValidationSummaryException e) {
//			instance was invalid
			isValid = false;
		}
		
		if(isValid){
			fail("The instance " + inst.getName() +" should be invalid!");
		}
		
	}
	
	
	private static String STRING_MAIN_FOLDER = "src/test/resources/com/github/oxygenPlugins/common/xsd/";
	private static String XSM_NAMESPACE ="http://www.schematron-quickfix.com/manipulator/process";
	
	private static XercesI createXerces(String path){
		
		try {
			return new XercesStream(getStreamSource(path));
		} catch (SAXException e) {
			return null;
		}
	}
	
	private static XercesI createXerces(File schemaFile){
		
		try {
			return new Xerces(schemaFile);
		} catch (SAXException | MalformedURLException e) {
			return null;
		}
	}
	
	private static XercesI createXerces(File schemaFile, String namespace){
		
		try {
			return new Xerces(namespace, schemaFile);
		} catch (SAXException | MalformedURLException e) {
			return null;
		}
	}
	
	
	private static InputStream getStreamSource(String path){
		return new Object().getClass().getResourceAsStream(path);
	}
}
