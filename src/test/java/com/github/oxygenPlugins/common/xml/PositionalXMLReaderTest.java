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
import com.github.oxygenPlugins.common.xml.staxParser.TextInfo;
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
	
	
	@Test
	public void testReadXMLTextSourceEntities() {
		try {
			String relativePath = "src/test/resources/com/github/oxygenPlugins/common/entities/entities.xml";
			
			File baseFile = new File(".");
			File srcFile = new File(baseFile, relativePath);
			
			Document doc = pxr.readXML(srcFile);
			TextInfo dtdInfo = (TextInfo) PositionalXMLReader.getNodeInfo(xpr.getNode("/root/dtd-ent/text()", doc));
			
//			Code:
//			text &with; entity and &another; an entitiy
//			Value
//			text w entity and another, but longer an entitiy
			
//			Entity 1:
//			start code: 6
//			start value: 6
//			end code: 11
//			end value: 6
			

//			Entity 2:
//			start code: 24
//			start value: 19
//			end code: 32
//			end value: 37
			
			testLocations(dtdInfo, 11, 14, 11, 57, srcFile.toURI().toString());
			
////			 before the entities
			assertEquals(2, dtdInfo.transformNodeToCode(2));

//			check the jump of the entity 1
			assertFalse(dtdInfo.isInJump(5));
			assertTrue(dtdInfo.isInJump(6));
			assertFalse(dtdInfo.isInJump(7));
			
//			the end of entity 1
			assertEquals(12, dtdInfo.transformNodeToCode(7));
			
//			start of entity 2
			assertEquals(23, dtdInfo.transformNodeToCode(18));
			
//			check the jump of entity 2
			assertFalse(dtdInfo.isInJump(18));
			assertTrue(dtdInfo.isInJump(19));
			assertTrue(dtdInfo.isInJump(37));
			assertFalse(dtdInfo.isInJump(38));
			

//			start of entity 2
			assertEquals(32, dtdInfo.transformNodeToCode(37));
			
			
			
			TextInfo escInfo = (TextInfo) PositionalXMLReader.getNodeInfo(xpr.getNode("/root/escape-ent/text()", doc));
			
//			Code:
//			characters like &amp; or &lt; and &gt; should be escaped.
//			Value
//			characters like & or < and > should be escaped.
			
//			Entity 1:
//			start code: 17
//			start value: 17
//			end code: 21
//			end value: 17
			

//			Entity 2:
//			start code: 26
//			start value: 22
//			end code: 29
//			end value: 22
			

//			Entity 3:
//			start code: 35
//			start value: 28
//			end code: 38
//			end value: 28

			Entity ent1 = new Entity(new int[]{17, 17, 21, 17});
			Entity ent2 = new Entity(new int[]{26, 22, 29, 22});
			Entity ent3 = new Entity(new int[]{35, 28, 38, 28});
			
			testLocations(escInfo, 12, 17, 12, 74, srcFile.toURI().toString());
			

			testEntities(escInfo, new Entity[]{ent1, ent2, ent3});
//			
//			 before the entities
			assertEquals(2, escInfo.transformNodeToCode(2));

//			check the jump of the entity 1
			assertFalse(escInfo.isInJump(16));
			assertTrue(escInfo.isInJump(17));
			assertFalse(escInfo.isInJump(18));
//			
//			the end of entity 1
			assertEquals(22, escInfo.transformNodeToCode(18));
			
//			start of entity 2
			assertEquals(25, escInfo.transformNodeToCode(21));
			
//			check the jump of entity 2
			assertFalse(escInfo.isInJump(21));
			assertTrue(escInfo.isInJump(22));
			assertFalse(escInfo.isInJump(23));
			

//			end of entity 2
			assertEquals(30, escInfo.transformNodeToCode(23));
			
			
			TextInfo uniInfo = (TextInfo) PositionalXMLReader.getNodeInfo(xpr.getNode("/root/unicode-ent/text()", doc));

//			Code:
//			special characters like &#xA0; can be inserted by there &#x003C;unicode position&#62;
//			Value
//			special characters like   can be inserted by there <unicode position>
			
//			Entity 1:
//			start code: 25
//			start value: 25
//			end code: 30
//			end value: 25

//			Entity 2:
//			start code: 57
//			start value: 52
//			end code: 64
//			end value: 52

//			Entity 3:
//			start code: 81
//			start value: 69
//			end code: 85
//			end value: 69
			
			testLocations(uniInfo, 13, 18, 13, 103, srcFile.toURI().toString());
			
			
			ent1 = new Entity(new int[]{25, 25, 30, 25});
			ent2 = new Entity(new int[]{57, 52, 64, 52});
			ent3 = new Entity(new int[]{81, 69, 85, 69});
			
			testEntities(uniInfo, new Entity[]{ent1, ent2, ent3});
			
			
			TextInfo mixInfo = (TextInfo) PositionalXMLReader.getNodeInfo(xpr.getNode("/root/mix/text()", doc));

//			Code:
//			&gt;They are marked with an &amp; followed by the # character and the unicode position in the format &hexa; or &decimal-format;. Closed by a ";" char. Example: &#x3C; which means &lt;.
//			Value
//			>They are marked with an & followed by the # character and the unicode position in the format xHxHxHxHx or 0000. Closed by a ";" char. Example: < which means <.
			
			ent1 = new Entity("&gt;",new int[]{1,1,4,1});
			ent2 = new Entity("&amp;",new int[]{29,26,33,26});
			ent3 = new Entity("&hexa;",new int[]{102,95,107,103});
			Entity ent4 = new Entity("&decimal-format;",new int[]{112,108,127,111});
			Entity ent5 = new Entity("&#x3C;",new int[]{161,145,166,145});
			Entity ent6 = new Entity("&lt;",new int[]{180,159,183,159});
			

			
			
			testEntities(mixInfo, new Entity[]{ent1, ent2, ent3, ent4, ent5, ent6});
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	
	private class Entity {
		Entity(){};
		Entity(String name, int[] coordinates){
			this(coordinates);
			this.name = name;
		}
		Entity(int[] coordinates){
			this.startCode = coordinates[0];
			this.startValue = coordinates[1];
			this.endCode = coordinates[2];
			this.endValue = coordinates[3];
		}
		
		String name = "";
		int startCode;
		int startValue;
		int endCode;
		int endValue;
	}
	
	
	private void testLocations(NodeInfo nodeInfo, int rowStart, int colStart, int rowEnd, int colEnd, String systemId ){

		assertEquals(rowStart, nodeInfo.getStart().getLineNumber());
		assertEquals(colStart, nodeInfo.getStart().getColumnNumber());
		assertEquals(rowEnd, nodeInfo.getEnd().getLineNumber());
		assertEquals(colEnd, nodeInfo.getEnd().getColumnNumber());
		assertEquals(systemId, nodeInfo.getStart().getSystemId());
	}
	
	private void testEntities(TextInfo nodeInfo, Entity[] entities){
		for (Entity entity : entities) {
//			start of entity 
			String name = entity.name;
			
			int returnTransformNodeToCode = nodeInfo.transformNodeToCode(entity.startValue - 1);
			if(entity.startCode - 1 !=returnTransformNodeToCode) {
				fail("Transform nodeToCode before entity " + name + "does not get the expected value!"
						+ "\nReturn value: " + returnTransformNodeToCode + ""
								+ "\nExpected value: " + (entity.startCode - 1));
			}
			
//			assertFalse(nodeInfo.isInJump(entity.startValue - 1));
//			assertTrue(nodeInfo.isInJump(entity.startValue));
//			assertTrue(nodeInfo.isInJump(entity.endValue));
//			assertFalse(nodeInfo.isInJump(entity.endValue + 1));
			
			if(nodeInfo.isInJump(entity.startValue - 1)){
				fail("The value position " + (entity.startValue - 1) + " should not be in jump (next to entity " +  name + ")");
			}
			if(!nodeInfo.isInJump(entity.startValue)){
				fail("The value position " + (entity.startValue) + " should be in jump (next to entity " +  name + ")");
			}
			if(!nodeInfo.isInJump(entity.endValue)){
				fail("The value position " + (entity.endValue) + " should be in jump (next to entity " +  name + ")");
			}
			if(nodeInfo.isInJump(entity.endValue + 1)){
				fail("The value position " + (entity.endValue + 1) + " should not be in jump (next to entity " +  name + ")");
			}

			returnTransformNodeToCode = nodeInfo.transformNodeToCode(entity.endValue + 1);
			
			if(entity.endCode + 1 !=returnTransformNodeToCode) {
				fail("Transform nodeToCode after entity " + name + "does not get the expected value!"
						+ "\nReturn value: " + returnTransformNodeToCode + ""
								+ "\nExpected value: " + (entity.endCode + 1));
			}
		}
		
	}
	
	private TextSource createTextSource(String xml){
		TextSource src = TextSource.createVirtualTextSource(new File("temp.xml"));
		src.setData(xml);
		return src;
	}

}
