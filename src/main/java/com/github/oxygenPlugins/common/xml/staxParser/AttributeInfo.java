package com.github.oxygenPlugins.common.xml.staxParser;

import javax.xml.stream.Location;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class AttributeInfo extends TextInfo {

	
	private final Location start;
	private final Location end;
	private final Location nameEnd;
	private final Location valueStart;
	private final LineColumnInfo lineColumns;
	
	protected AttributeInfo(Attr node) {
		super(node);
		DocumentInfo docInfo = (DocumentInfo) PositionalXMLReader.getNodeInfo(node.getOwnerDocument());
		this.lineColumns = docInfo.getLineColumns();
//		ElementInfo parentInfo = PositionalXMLReader.getNodeInfo(node.getOwnerElement());
//		String attributeRegion = parentInfo.getAttributRegion();
//		Location[] startEnd = getAttributStartEnd(attributeRegion, parentInfo.getAttributRegionStart(), node, lineColumns);
		start = (Location) node.getUserData(PositionalXMLReader.NODE_LOCATION_START);
		end = (Location) node.getUserData(PositionalXMLReader.NODE_LOCATION_END);;
		nameEnd = (Location) node.getUserData(PositionalXMLReader.NODE_INNER_LOCATION_START);
		Location valStartloc = (Location) node.getUserData(PositionalXMLReader.NODE_INNER_LOCATION_END);
		valueStart = NodeInfo.newLocation(valStartloc, 1, lineColumns);
	}
	public Node getNode() {
		return node;
	}
	@Override
	public Location getValueStart(){
		return valueStart;
	}
	
	

	public Location getNameEnd(){
		return nameEnd;
	}

	public Location getStart() {
		
		return start;
	}
	
	public int getStartOffset(){
		return start.getCharacterOffset();
	}

	public Location getEnd() {
		return end;
	}
	
	public int getEndOffset(){
		return end.getCharacterOffset();
	}
	
//	public NodeInfo nodeInfoWithOffset(int start, int end, LineColumnInfo lineColumns) {
////		int valueLength = this.node.getNodeValue().length();
////		
////		int valStartOffset = this.getEndOffset() - valueLength - 1;
////		int jump = valStartOffset - this.getStartOffset();
////		
////		start += jump;
////		end += jump;
//		
////		Location newStart = newLocation(this.getStart(), start, start, 0);
////		Location newEnd = newLocation(this.getStart(), end, end, 0);
////		
////		NodeInfo ni = new NodeInfo(this.node, newStart, newEnd);
//		return this;
//	}

	

//	private final Pattern attrPattern = Pattern
//			.compile("\\s+(\\S+?)(\\s+)?=(\\s+)?(\"([^\"]*)\"|'([^']*)')");
//	private Location[] getAttributStartEnd(String attributRegion, Location locStart, Node attr, LineColumnInfo lineColumns) {
//		Location[] startEnd = new Location[2];
//		Matcher matcher = attrPattern.matcher(attributRegion);
////		debug-error! can not be debugged...
//		while (matcher.find()) {
//			String name = matcher.group(1);
//			
//			if (name.equals(attr.getNodeName())) {
//
//				String valueCode = matcher.group(5);
//				valueCode = valueCode == null ? matcher.group(6) : valueCode;
//				
//				parseJumps(valueCode, this.node.getNodeValue());
//				
//				MatchResult mresult = matcher.toMatchResult();
//				int startPos = mresult.start();
//				int endPos = mresult.end();
//				startEnd[0] = NodeInfo.newLocation(locStart, startPos, lineColumns);
//				startEnd[1] = NodeInfo.newLocation(locStart, endPos, lineColumns);
//				break;
//			}
//		}
//		return startEnd;
//	}
	
//	private void parseJumps(String code, String value){
//		parseJumps(code, value, 0);
//	}
//	private void parseJumps(String code, String value, int start){
//		if(code.contains("&")){
//			int startJump = code.indexOf('&');
//			String fromEntityCode = code.substring(startJump);
//			String fromEntityValue = code.substring(startJump);
//		} else {
//			return;
//		}
//		
//	}
}
