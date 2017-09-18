package com.github.oxygenPlugins.common.xml.staxParser;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.github.oxygenPlugins.common.text.TextSource;
import com.github.oxygenPlugins.common.xml.xpath.XPathReader;

public class StringNodeComparer {
	public static class CompareConfig {
		public boolean codeSensitive = false;
		public boolean documentPath = false;
		public boolean attributeOrderSensitive = false;
		public boolean ignoreWhitespaceOnlyNodes = true;
		public boolean ignoreBuildInAttributes = true;
	}

	public static String getCompareString(TextSource xmlCode, CompareConfig config) {
		if (config.codeSensitive) {
			String compareString = config.documentPath ? xmlCode.getFile().getAbsolutePath() + "\n" : "";
			compareString += xmlCode.toString();
			return compareString;
		} else {
			StringNode xmlSN;
			try {
				xmlSN = new StringNode(xmlCode);
				return getCompareString(xmlSN, config);
			} catch (IOException | SAXException | XMLStreamException e) {
				return "ERROR: " + e.getMessage();
			}
		}
	}

	public static String getCompareString(StringNode xmlCode, CompareConfig config) {
		String compareString = config.documentPath ? xmlCode.getFile().getAbsolutePath() + "\n" : "";
		if (config.codeSensitive) {
			compareString += xmlCode.toString();
		} else {
			compareString += getCompareString(xmlCode.getDocument(), config);
		}

		return compareString;
	}

	private static String getCompareString(Document doc, CompareConfig config) {
		return getCompareInnerString(doc, config);
	}

	private static String getCompareString(Element el, CompareConfig config) {
		String elementName = el.getPrefix() == null ? "" : el.getPrefix() + ":";
		elementName += el.getLocalName();
		String attrString = getCompareAttributeString(el, config);
		return "<" + elementName + attrString + ">\n" + getCompareInnerString(el, config) + "</" + elementName + ">\n";
	}
	
	private static String getCompareAttributeString(Element el, CompareConfig config) {
		ArrayList<String> attributeStrings = new ArrayList<String>();
		NamedNodeMap attrMap = el.getAttributes();
		
		for (int i = 0; i < attrMap.getLength(); i++) {
			Node a = attrMap.item(i);
			attributeStrings.add(getCompareString(a, config));
		}
		String joined = "";
		for (String string : attributeStrings) {
			joined += " " + string;
		}
		
		return joined;
	}

	private static String getCompareString(ProcessingInstruction pi, CompareConfig config) {
		String piName = pi.getLocalName();
		return "<?" + piName + " " + pi.getNodeValue() + "?>\n";
	}

	private static String getCompareString(Comment comment, CompareConfig config) {
		return "<!--" + comment.getNodeValue() + "-->\n";
	}
	
	private static String getCompareString(Attr attr, CompareConfig config) {
		
		AttributeInfo ai = PositionalXMLReader.getNodeInfo(attr);
		if(!ai.isValid() && config.ignoreBuildInAttributes){
			return "";
		}
		String attrName = attr.getPrefix() == null ? "" : attr.getPrefix() + ":";
		attrName += attr.getName();
		return attrName + "=\"" + attr.getValue() + "\"";
	}

	private static String getCompareString(Text text, CompareConfig config) {
		String value = text.getNodeValue();
		if (config.ignoreWhitespaceOnlyNodes) {
			if (value.matches("^\\s+$")) {
				return "";
			}
		}
		return value;
	}

	private static String getCompareString(Node node, CompareConfig config) {
		if (node instanceof Element) {
			return getCompareString((Element) node, config);
		} else if (node instanceof ProcessingInstruction) {
			return getCompareString((ProcessingInstruction) node, config);
		} else if (node instanceof Comment) {
			return getCompareString((Comment) node, config);
		} else if (node instanceof Text) {
			return getCompareString((Text) node, config);
		} else if (node instanceof Attr) {
			return getCompareString((Attr) node, config);
		}

		return node.getNodeValue() + getCompareInnerString(node, config);
	}

	private static String getCompareInnerString(Node node, CompareConfig config) {
		XPathReader xpr = new XPathReader();
		try {
			ArrayList<Node> childs = xpr.getNodeList("./node()", node);
			String compareString = "";
			for (Node child : childs) {
				compareString += getCompareString(child, config);
			}
			return compareString;
		} catch (XPathExpressionException e) {
			return "ERROR: " + e.getMessage();
		}
	}

}
