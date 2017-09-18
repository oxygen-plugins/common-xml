package com.github.oxygenPlugins.common.xml.staxParser;

// PositionalXMLReader.java

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.github.oxygenPlugins.common.text.TextSource;

public class PositionalXMLReader {

	public final static String NODE_INNER_LOCATION_START = "nils";
	public final static String NODE_INNER_LOCATION_END = "nile";
	public static final String NODE_INNER_JUMP_RANGES = "nijr";

	public final static String NODE_LOCATION_START = "nls";
	public final static String NODE_LOCATION_END = "nle";

	public final static String ATTRIBUTE_REGION_LOCATION_START = "arls";
	public final static String ATTRIBUTE_REGION_LOCATION_END = "arle";

	public final static String DTD_LOCATION_START = "dls";
	public final static String DTD_LOCATION_END = "dle";

	public static final String XML_DECL_LOCATION_START = "xdls";
	public static final String XML_DECL_LOCATION_END = "xdle";

	public static final String DOCUMENT_LINE_COLUMNS = "dlc";

	public static final String ATTRIBUT_REGION = "ar";

	public final static int STANDARD_TYPE = 0;
	public final static int ELEMENT_START_TYPE = 1;
	public final static int ELEMENT_END_TYPE = 2;
	public final static int ATTRIBUTE_TYPE = 3;
	public final static int TEXT_TYPE = 4;
	public final static int DTD_TYPE = 5;
	public static final int DTD_TYPE_END = 6;

	public static final String HAS_DTD = "hasDTD";

	public static final String NAMESPACE_CONTEXT = "nsc";

	private final static String[] ALL_USERDATA_KEYS = new String[] 
			{ 
					NODE_LOCATION_START, 
					NODE_LOCATION_END,
					ATTRIBUT_REGION,
					ATTRIBUTE_REGION_LOCATION_START,
					ATTRIBUTE_REGION_LOCATION_END,
					NODE_INNER_LOCATION_START, 
					NODE_INNER_LOCATION_END,
					NODE_INNER_JUMP_RANGES, 
					NAMESPACE_CONTEXT,
					HAS_DTD,
					DTD_LOCATION_START,
					DTD_LOCATION_END,
					XML_DECL_LOCATION_START,
					XML_DECL_LOCATION_END,
					DOCUMENT_LINE_COLUMNS
			};
	// public static final String PREFIX_MAPPING = "prefixMapping";
	// public static final String NAMESPACE_MAPPING = "namespaceMapping";

	// private final HashMap<Integer, Integer> linePositionMap = new
	// HashMap<Integer, Integer>();
	// private int lineCounter = 0;

	private final PositionalStaxParser woodStoxParser = initialParser();

	private static PositionalStaxParser initialParser() {
		return new PositionalStaxParser();
	}

	public static int getPosition(Node node, String userDataKey) {
		return getPosition(getNodeInfo(node), userDataKey);
	}

	private static int getPosition(NodeInfo info, String userDataKey) {
		if (userDataKey.equals(NODE_LOCATION_START)) {
			return info.getStartOffset();
		}
		if (userDataKey.equals(NODE_LOCATION_END)) {
			return info.getEndOffset();
		}
		if (info instanceof ElementInfo) {
			if (userDataKey.equals(NODE_INNER_LOCATION_END)) {
				return ((ElementInfo) info).getInnerEndOffset();
			}
			if (userDataKey.equals(NODE_INNER_LOCATION_START)) {
				return ((ElementInfo) info).getInnerStartOffset();
			}
			if (userDataKey.equals(ATTRIBUTE_REGION_LOCATION_START)) {
				return ((ElementInfo) info).getAttributRegionStartOffset();
			}
			if (userDataKey.equals(ATTRIBUTE_REGION_LOCATION_END)) {
				return ((ElementInfo) info).getAttributRegionEndOffset();
			}
		}
		if (info instanceof DocumentInfo) {
			if (userDataKey.equals(XML_DECL_LOCATION_START)) {
				return ((DocumentInfo) info).getXmlDeclStart().getCharacterOffset();
			}
			if (userDataKey.equals(XML_DECL_LOCATION_END)) {
				return ((DocumentInfo) info).getXmlDeclEnd().getCharacterOffset();
			}
			if (userDataKey.equals(DTD_LOCATION_START)) {
				return ((DocumentInfo) info).getDtdStart().getCharacterOffset();
			}
			if (userDataKey.equals(DTD_LOCATION_END)) {
				return ((DocumentInfo) info).getDtdEnd().getCharacterOffset();
			}
		}
		return -1;
	}

	public static int getLine(Node node, String userDataKey) {
		Location loc = (Location) node.getUserData(userDataKey);
		return loc.getLineNumber();
	}

	public PositionalXMLReader() {

	}

	private boolean isWellformed = true;

	public boolean isWellformed(final TextSource source) {
		return isWellformed(source.toString());
	}

	public boolean isWellformed(final String source) {
		final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		this.isWellformed = true;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			docBuilder.setErrorHandler(new ErrorHandler() {
				public void warning(SAXParseException arg0) throws SAXException {

				}

				public void fatalError(SAXParseException arg0) throws SAXException {
					isWellformed = false;
				}

				public void error(SAXParseException arg0) throws SAXException {
					isWellformed = false;
				}
			});
			docBuilder.parse(new InputSource(new StringReader(source)));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return isWellformed;
	}

	public Document readXML(final File doc) throws IOException, SAXException, XMLStreamException {
		TextSource ts = TextSource.readTextFile(doc);
		return this.readXML(ts);
	}

	public Document readXML(final TextSource source) throws IOException, SAXException, XMLStreamException {

		// positionMap(source.toString());

		final Document docNode;

		final ArrayList<Node> allNodes = new ArrayList<Node>();

		final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			docNode = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Can't create SAX parser / DOM builder.", e);
		}

		// encoding
		woodStoxParser.parse(docNode, source);

		docNode.setUserData("allNodes", allNodes, null);
		// docNode.setUserData("positionNodeMap", posHandler.positionNodeMap(),
		// null);

		return docNode;
	}

	public static Location getLocation(Node node, String key) {
		return (Location) node.getUserData(key);
	}

	public static ElementInfo getNodeInfo(Element node) {
		return new ElementInfo(node);
	}

	public static NodeInfo getNodeInfo(Node node) {
		if (node instanceof Element) {
			return getNodeInfo((Element) node);
		}
		if (node instanceof Attr) {
			return getNodeInfo((Attr) node);
		}
		if (node instanceof Document) {
			return getNodeInfo((Document) node);
		}
		if (node instanceof Text) {
			return getNodeInfo((Text) node);
		}
		return new NodeInfo(node);
	}

	public static TextInfo getNodeInfo(Text node) {
		return new TextInfo(node);
	}

	private static DocumentInfo getNodeInfo(Document node) {
		return new DocumentInfo(node);
	}

	public static AttributeInfo getNodeInfo(Attr node) {
		return new AttributeInfo(node);
	}

	public static ArrayList<NodeInfo> getNodeInfo(NodeList nodes) {
		ArrayList<NodeInfo> nodeInfos = new ArrayList<NodeInfo>();
		for (int i = 0; i < nodes.getLength(); i++) {
			nodeInfos.add(getNodeInfo(nodes.item(i)));
		}
		return nodeInfos;
	}

	public static Node transferNode(Document targetDoc, Node node) {
		Node targetNode = targetDoc.importNode(node, true);
		copyUserData(node, targetNode, true);
		return targetNode;
	}
	private static void copyUserData(Node from, Node to) {
		for (String key : ALL_USERDATA_KEYS) {
			Object value = from.getUserData(key);
			if(value != null){
				to.setUserData(key, value, null);
			}
		}
	}

	private static void copyUserData(Node from, Node to, boolean deep) {
		copyUserData(from, to);
		if(deep){
			NodeList childFrom = from.getChildNodes();
			
			NamedNodeMap attrFrom = from.getAttributes();
			NamedNodeMap attrTo = to.getAttributes();
			
			NodeList childTo = to.getChildNodes();
			for (int i = 0; i < childFrom.getLength(); i++) {
				copyUserData(childFrom.item(i), childTo.item(i), true);
			}
			if(attrFrom != null){
				for (int i = 0; i < attrFrom.getLength(); i++) {
					copyUserData(attrFrom.item(i), attrTo.item(i));
				}
			}
			
			
		}
	}

}