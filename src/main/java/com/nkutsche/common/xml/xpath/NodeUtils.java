package com.nkutsche.common.xml.xpath;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeUtils {
	
	public static ArrayList<Node> toArrayList(NodeList nodeSet) {
		ArrayList<Node> nodeList = new ArrayList<Node>();

		for (int i = 0; i < nodeSet.getLength(); i++) {
			nodeList.add(nodeSet.item(i));
		}
		return nodeList;
	}
	
	public static Node[] toArray(NodeList nodeSet){
		return toArray(toArrayList(nodeSet));
	}
	
	public static Node[] toArray(ArrayList<Node> nodeList){
		return nodeList.toArray(new Node[nodeList.size()]);
	}
}
