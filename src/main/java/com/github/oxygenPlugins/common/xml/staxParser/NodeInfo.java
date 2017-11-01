package com.github.oxygenPlugins.common.xml.staxParser;

import javax.xml.stream.Location;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

import com.github.oxygenPlugins.common.xml.xpath.XPathReader;

public class NodeInfo {
	protected final Node node;
	private final Location start;
	private final Location end;
	
	public static XPathReader XPR = new XPathReader();

	protected NodeInfo(Node node, Location start, Location end) {
		this.node = node;
		this.start = start;
		this.end = end;
	}

	protected NodeInfo(Node node) {
		this(node, PositionalXMLReader.getLocation(node,
				PositionalXMLReader.NODE_LOCATION_START), PositionalXMLReader
				.getLocation(node, PositionalXMLReader.NODE_LOCATION_END));
	}

	public Node getNode() {
		return node;
	}

	public Location getStart() {
		return start;
	}

	public int getStartOffset() {
		return start.getCharacterOffset();
	}

	public Location getEnd() {
		return end;
	}

	public int getEndOffset() {
		return end.getCharacterOffset();
	}

	public int getLength() {
		return this.getEndOffset() - this.getStartOffset();
	}

	public Location getMarkStartLocation() {
		return this.getStart();
	}

	public Location getMarkEndLocation() {
		return this.getEnd();
	}

	public int getMarkStart() {
		return this.getStartOffset();
	}

	public int getMarkEnd() {
		return this.getEndOffset();
	}
	
	public Location getValueStart(){
		return getStart();
	}
	
	public int getValueStartOffset(){
		return getValueStart().getCharacterOffset();
	}
	
	public String getBaseURI(){
		return getBaseURI(this.node);
	}
	
	private String getBaseURI(Node node){
		if(node == null)
			return this.start.getSystemId(); 
		String uri = node.getBaseURI();
		
		if(uri == null)
			return getBaseURI(node.getParentNode());
		
		return uri;
		
	}
	
	public NodeInfo nodeInfoWithOffset(int start, int end, LineColumnInfo lineColumns) {
		
		if(this.getNode() instanceof Comment){
			start += 4;
			end += 4;
		} else if(this.getNode() instanceof ProcessingInstruction){
			int valueLength = this.getNode().getNodeValue().length();
			
			int valStartOffset = this.getEndOffset() - valueLength - 2;
			int jump = valStartOffset - this.getStartOffset();
			
			start += jump;
			end += jump;
		}
		
		Location newStart = newLocation(this.getValueStart(), start, lineColumns);
		Location newEnd = newLocation(this.getValueStart(), end, lineColumns);
		
		NodeInfo ni = new NodeInfo(this.node, newStart, newEnd);
		return ni;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.node.toString() + ": [start:" + this.getStartOffset()
				+ "; end:" + this.getEndOffset() + "]";
	}
	
//	private static Location newLocation(final Location loc, int addPositon) {
//		return newLocation(loc, addPositon, null);
//	}
	protected static Location newLocation(final Location loc, int addPositon, LineColumnInfo lineColumns) {
//		final int correctLineNumber = loc.getLineNumber() + addLine;
//		final int correctColumnNumber = loc.getColumnNumber() + addColumn;
		final int correctPosition = loc.getCharacterOffset() + addPositon;
		int[] lineColumn;
		if(lineColumns != null){
			lineColumn = lineColumns.getLineColumn(correctPosition);
		} else {
			lineColumn = new int[]{loc.getLineNumber(), -1};
		}
		final int correctLineNumber = lineColumn[0];
		final int correctColumnNumber = lineColumn[1];
		return new Location() {

			public String getSystemId() {
				// TODO Auto-generated method stub
				return loc.getSystemId();
			}

			public String getPublicId() {
				// TODO Auto-generated method stub
				return loc.getPublicId();
			}

			public int getLineNumber() {
				// TODO Auto-generated method stub
				return correctLineNumber;
			}

			public int getColumnNumber() {
				// TODO Auto-generated method stub
				return correctColumnNumber;
			}

			public int getCharacterOffset() {
				// TODO Auto-generated method stub
				return correctPosition;
			}
		};
	}
}
