package com.github.oxygenPlugins.common.xml.staxParser;

import java.util.ArrayList;

import com.github.oxygenPlugins.common.text.TextSource;


public class LineColumnInfo {
	ArrayList<Integer> lineLengths = new ArrayList<Integer>();
	ArrayList<Integer> lineOffsets = new ArrayList<Integer>();
	
	public LineColumnInfo(String text, String lineSeparator){
		updateLineColumns(text, lineSeparator);
	}
	
	public LineColumnInfo(StringNode sn) {
		updateLineColumns(sn);
	}

	public void updateLineColumns(String text, String lineSeparator){
		int offset = 0;
		while (text.length() > 0) {
			lineOffsets.add(offset);
			int i = text.indexOf(lineSeparator);
			offset += i + 1;
			lineLengths.add(i);
			text = text.substring(i + 1);
			if(!text.contains(lineSeparator)){
				break;
			}
		}
	}
	public void updateLineColumns(StringNode sn){
		this.lineLengths = new ArrayList<Integer>();
		this.lineOffsets = new ArrayList<Integer>();
		TextSource textSource = sn.getTextSource();
		if(textSource != null){
			String sep = textSource.getLineSeperator();
			String text = textSource.toString();
			updateLineColumns(text, sep);
		}
	} 
	
	public int[] getLineColumn(int offset){
		int l = 0;
		int c = -1;
		int lOffsetBef = 0;
		for (int lOffset : lineOffsets) {
			if(lOffset > offset){
				c = offset - lOffsetBef + 1;
				break;
			} else {
				lOffsetBef = lOffset;
			}
			l++;
		}
		return new int[]{l, c};
	}
	
	@Override
	public String toString() {
		String result = "{";
		for (int offset : this.lineOffsets) {
			result += offset + ", ";
		}
		result += "}";
		return result;
	}
}
