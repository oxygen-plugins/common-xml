package com.github.oxygenPlugins.common.xml.staxParser;

import javax.xml.stream.Location;

import org.w3c.dom.Document;

public class DocumentInfo extends NodeInfo {
	private final Location xmlDeclStart;
	private final Location xmlDeclEnd;
	private final Location dtdStart;
	private final Location dtdEnd;
	private final LineColumnInfo lineColumns;

	protected DocumentInfo(Document node) {
		super(node);
		xmlDeclStart = (Location) node.getUserData(PositionalXMLReader.XML_DECL_LOCATION_START);
		xmlDeclEnd = (Location) node.getUserData(PositionalXMLReader.XML_DECL_LOCATION_END);
		dtdStart = (Location) node.getUserData(PositionalXMLReader.DTD_LOCATION_START);
		dtdEnd = (Location) node.getUserData(PositionalXMLReader.DTD_LOCATION_END);
		lineColumns = (LineColumnInfo) node.getUserData(PositionalXMLReader.DOCUMENT_LINE_COLUMNS);
	}
	
	public boolean hasXmlDecl(){
		return xmlDeclStart != null;
	} 
	
	public Location getXmlDeclStart(){
		return xmlDeclStart;
	}
	public Location getXmlDeclEnd(){
		return xmlDeclEnd;
	}

	public Location getDtdStart() {
		return dtdStart;
	}

	public Location getDtdEnd() {
		return dtdEnd;
	}
	
	public boolean hasDtd(){
		return dtdStart != null;
	}

	public LineColumnInfo getLineColumns() {
		return lineColumns;
	}
	

}
