package com.github.oxygenPlugins.common.xml.staxParser;

import javax.xml.stream.Location;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.oxygenPlugins.common.xml.staxParser.TextInfo.JumpInfo;

public class ElementInfo extends NodeInfo {

	private Location innerStart;
	private Location innerEnd;
	private Location attrRegStart;
	private Location attrRegEnd;
	private String attributRegion;

	protected ElementInfo(Element node) {
		super(node);
		attributRegion = (String) node.getUserData(PositionalXMLReader.ATTRIBUT_REGION);
		if(attributRegion == null)
			attributRegion = "";
		innerStart = PositionalXMLReader.getLocation(node,
				PositionalXMLReader.NODE_INNER_LOCATION_START);
		innerEnd = PositionalXMLReader.getLocation(node,
				PositionalXMLReader.NODE_INNER_LOCATION_END);

		attrRegStart = PositionalXMLReader.getLocation(node, PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_START);
		attrRegEnd = PositionalXMLReader.getLocation(node, PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_END);
	}

	public Location getInnerStart() {
		return innerStart;
	}

	public int getInnerStartOffset() {
		return innerStart.getCharacterOffset();
	}

	public Location getInnerEnd() {
		return innerEnd;
	}

	public int getInnerEndOffset() {
		return innerEnd.getCharacterOffset();
	}

	public Location getAttributRegionStart() {
		return attrRegStart;
	}

	public int getAttributRegionStartOffset() {
		return attrRegStart.getCharacterOffset();
	}

	public Location getAttributRegionEnd() {
		return attrRegEnd;
	}

	public int getAttributRegionEndOffset() {
		return attrRegEnd.getCharacterOffset();
	}

	public Location getLocation(String key) {
		return PositionalXMLReader.getLocation(this.getNode(), key);
	}

	public int getLocationOffset(String key) {
		return PositionalXMLReader.getLocation(this.getNode(), key)
				.getCharacterOffset();
	}

	public Location getMarkEndLocation(){
		return this.getInnerStart();
	}
	public int getMarkEnd() {
		return this.getInnerStartOffset();
	}
	public String getAttributRegion(){
		return this.attributRegion;
	}
	
	@Override
	public NodeInfo nodeInfoWithOffset(int start, int end, LineColumnInfo lineColumns) {
		try {
			Location newStart = null;
			Location newEnd = null;
			int orgStart = start;
			int orgEnd = end;
			NodeList textContent = NodeInfo.XPR.getNodeSet(".//text()", this.getNode());
			int contentLength = 0;
			for (int i = 0; i < textContent.getLength(); i++) {
				Node textNode = textContent.item(i);
				int contentLengthBef = contentLength;
				contentLength += textNode.getNodeValue().length();
				TextInfo textInfo = (TextInfo) PositionalXMLReader.getNodeInfo(textNode);

				if(contentLengthBef <= orgStart && contentLength > orgStart){
					int innerTextOffset = orgStart - contentLengthBef;
					
					if(textInfo.isInJump(innerTextOffset)){
						JumpInfo jumpInfo = textInfo.getJumpInfo(innerTextOffset);
						start = jumpInfo.startOffset;
					} else {
						start = textInfo.transformNodeToCode(innerTextOffset);
					}
					
					newStart = newLocation(textInfo.getStart(), start, lineColumns);
					
//					int tiStart = textInfo.getStartOffset();
//					int jump = tiStart - (contentLengthBef + this.getStartOffset());
//					start += jump;
				}
				if(contentLengthBef < orgEnd && contentLength >= orgEnd){
					
					int innerTextOffset = orgEnd - contentLengthBef;
					
					if(textInfo.isInJump(innerTextOffset)){
						JumpInfo jumpInfo = textInfo.getJumpInfo(innerTextOffset);
						end = jumpInfo.endOffset;
					} else {
						end = textInfo.transformNodeToCode(innerTextOffset);
					}
					
					newEnd = newLocation(textInfo.getStart(), end, lineColumns);
					
					
//					NodeInfo ti = PositionalXMLReader.getNodeInfo(textNode);
//					int tiStart = ti.getStartOffset();
//					int jump = tiStart - (contentLengthBef + this.getStartOffset());
//					end += jump;
				}
			}
			
//			newStart = newLocation(this.getStart(), start, lineColumns);
//			newEnd = newLocation(this.getStart(), end, lineColumns);
			if(newStart == null || newEnd == null)
				return super.nodeInfoWithOffset(start, end, lineColumns);
			return new NodeInfo(this.node, newStart, newEnd);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
		
	}

//	private Location[] getAttributRegionStartEnd(Element el) {
//		Location[] startEnd = new Location[2];
//		
//		// if(el.hasAttributes()){
//
//		Pattern patternStart = Pattern.compile("\\s");
//		Matcher matcherStart = patternStart.matcher(attributRegion);
//
//		Pattern patternEnd = Pattern.compile("(/)?>$", Pattern.DOTALL);
//		Matcher matcherEnd = patternEnd.matcher(attributRegion);
//
//		int addEndPos = matcherEnd.find() ? matcherEnd.start() : attributRegion
//				.length() - 1;
//		int addStartPos = matcherStart.find() ? matcherStart.start()
//				: addEndPos;
//		
//		startEnd[0] = NodeInfo.newLocation(this.getStart(), addStartPos);
//		startEnd[1] = NodeInfo.newLocation(this.getStart(), addEndPos);
//		
//		return startEnd;
//		// } else {
//		// el.setUserData(PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_START,
//		// newLocation(range.end, -1), null);
//		// el.setUserData(PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_END,
//		// newLocation(range.end, -1), null);
//		// }
//
//	}
}
