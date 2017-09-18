package com.github.oxygenPlugins.common.xml;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.oxygenPlugins.common.text.TextSource;
import com.github.oxygenPlugins.common.xml.staxParser.NodeInfo;
import com.github.oxygenPlugins.common.xml.staxParser.PositionalXMLReader;
import com.github.oxygenPlugins.common.xml.xpath.XPathReader;

public class PositionalXMLReaderTest {
	PositionalXMLReader pxr = new PositionalXMLReader();
	XPathReader xpr = new XPathReader();

	@Test
	public void testIsWellformedString() {
//		wellformed:
		assertTrue(pxr.isWellformed("<root/>"));
		assertTrue(pxr.isWellformed("<root>&amp;</root>"));
		assertTrue(pxr.isWellformed("<root>&lt;</root>"));
		assertTrue(pxr.isWellformed("<root>></root>"));

//		not wellformed
		assertFalse(pxr.isWellformed("<root>"));
		assertFalse(pxr.isWellformed("<root>&ent;</root>"));
		assertFalse(pxr.isWellformed("<root><</root>"));
	}

	@Test
	public void testReadXMLTextSourceSimple() {
		try {
			Document doc = pxr.readXML(new File("src/test/resources/com/github/oxygenPlugins/common/xsd/Test1-simple-invalid.xml"));
			NodeInfo e1Ni = PositionalXMLReader.getNodeInfo(xpr.getNode("/root/element1[1]", doc));
			NodeInfo t1Ni = PositionalXMLReader.getNodeInfo(xpr.getNode("/root/element1[1]/text()", doc));
			
			assertEquals(4, e1Ni.getStart().getLineNumber());
			assertEquals(5, e1Ni.getStart().getColumnNumber());
			assertEquals(4, e1Ni.getEnd().getLineNumber());
			assertEquals(29, e1Ni.getEnd().getColumnNumber());
			

			assertEquals(4, t1Ni.getStart().getLineNumber());
			assertEquals(15, t1Ni.getStart().getColumnNumber());
			assertEquals(4, t1Ni.getEnd().getLineNumber());
			assertEquals(18, t1Ni.getEnd().getColumnNumber());
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
//	@Test
	public void testReadXMLTextSourceComplex() {
		try {
			String relativePath = "src/test/resources/com/github/oxygenPlugins/common/xsd/Test2-complex-invalid.xml";
			File baseFile = new File(".");
			File srcFile = new File(baseFile, relativePath);
			
			Document doc = pxr.readXML(srcFile);
			NodeInfo e1Ni = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[1]", doc));
			NodeInfo a1Ni = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[1]/@*[1]", doc));
			
			assertEquals(3, e1Ni.getStart().getLineNumber());
			assertEquals(5, e1Ni.getStart().getColumnNumber());
			assertEquals(3, e1Ni.getEnd().getLineNumber());
			assertEquals(53, e1Ni.getEnd().getColumnNumber());
			assertEquals(srcFile.toURI().toString(), e1Ni.getStart().getSystemId());
			

			assertEquals(3, a1Ni.getStart().getLineNumber());
			assertEquals(16, a1Ni.getStart().getColumnNumber());
			assertEquals(3, a1Ni.getEnd().getLineNumber());
			assertEquals(51, a1Ni.getEnd().getColumnNumber());
			assertEquals(srcFile.toURI().toString(), a1Ni.getStart().getSystemId());
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testReadXMLTextSourceXInclude() {
		try {
			String relativePath = "src/test/resources/com/github/oxygenPlugins/common/xinclude/xinclude1.xml";
			
			File baseFile = new File(".");
			File srcFile = new File(baseFile, relativePath);
			URI includeUri = srcFile.toURI().resolve("xinclude1_incuded.xml");
			
			
			Document doc = pxr.readXML(srcFile);
			NodeInfo e1Ni = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[1]", doc));
			NodeInfo e1AidNi = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[1]/@*[2]", doc));

			NodeInfo e1c1Ni = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[1]/*[1]", doc));
			NodeInfo e1c2Ni = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[1]/*[2]", doc));
			

			NodeInfo e1c1AidNi = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[1]/*[1]/@id", doc));
			NodeInfo e1c2AidNi = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[1]/*[2]/@id", doc));
			
			NodeInfo e2Ni = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[2]", doc));
			
			testLocations(e1Ni, 2, 1, 5, 11, includeUri.toString());
			
			testLocations(e1AidNi, 2, 9, 2, 17, includeUri.toString());
			
			testLocations(e1c1Ni, 3, 5, 3, 39, includeUri.toString());
			testLocations(e1c2Ni, 4, 5, 4, 39, includeUri.toString());
			
			testLocations(e1c1AidNi, 3, 7, 3, 24, includeUri.toString());
			testLocations(e1c2AidNi, 4, 20, 4, 37, includeUri.toString());
			
			testLocations(e2Ni, 4, 5, 4, 23, srcFile.toURI().toString());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReadXMLTextSourceXIncludeFallback() {
		try {
			String relativePath = "src/test/resources/com/github/oxygenPlugins/common/xinclude/xinclude2.xml";
			
			File baseFile = new File(".");
			File srcFile = new File(baseFile, relativePath);
			
			Document doc = pxr.readXML(srcFile);
			NodeInfo e1Ni = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[1]", doc));
//			NodeInfo a1Ni = PositionalXMLReader.getNodeInfo(xpr.getNode("/*/*[1]/@*[1]", doc));
			
			testLocations(e1Ni, 4, 22, 4, 40, srcFile.toURI().toString());
			
//			assertEquals(srcFile.toURI().toString(), e1Ni.getStart().getSystemId());
			

//			assertEquals(3, a1Ni.getStart().getLineNumber());
//			assertEquals(16, a1Ni.getStart().getColumnNumber());
//			assertEquals(3, a1Ni.getEnd().getLineNumber());
//			assertEquals(51, a1Ni.getEnd().getColumnNumber());
//			assertEquals(srcFile.toURI().toString(), a1Ni.getStart().getSystemId());
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private void testLocations(NodeInfo nodeInfo, int rowStart, int colStart, int rowEnd, int colEnd, String systemId ){

		assertEquals(rowStart, nodeInfo.getStart().getLineNumber());
		assertEquals(colStart, nodeInfo.getStart().getColumnNumber());
		assertEquals(rowEnd, nodeInfo.getEnd().getLineNumber());
		assertEquals(colEnd, nodeInfo.getEnd().getColumnNumber());
		assertEquals(systemId, nodeInfo.getStart().getSystemId());
	}
	
	private TextSource createTextSource(String xml){
		TextSource src = TextSource.createVirtualTextSource(new File("temp.xml"));
		src.setData(xml);
		return src;
	}

}
