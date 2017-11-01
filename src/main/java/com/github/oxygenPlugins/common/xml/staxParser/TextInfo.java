package com.github.oxygenPlugins.common.xml.staxParser;


import org.w3c.dom.Node;

import com.github.oxygenPlugins.common.xml.staxParser.UserDataManager.JumpRange;

public class TextInfo extends NodeInfo {
	
	
	private JumpRange[] jumps = null;

	protected TextInfo(Node node) {
		super(node);
		Object jumpsObj = node.getUserData(PositionalXMLReader.NODE_INNER_JUMP_RANGES);
		if(jumpsObj != null){
			this.jumps = (JumpRange[]) jumpsObj;
		}
		
	}
	
	@Override
	public String getBaseURI() {
		String superBaseUri = super.getBaseURI();
		if(superBaseUri != null)
			return null;
		return this.getNode().getParentNode().getBaseURI();
	}
	
	public int transformNodeToCode(int offset){
		return translate(offset);
	}
	
	
	
	private int translate(int offset){
		if(jumps == null)
			return -1;
		offset += this.getValueStartOffset();
		int diff = 0;
		for (JumpRange jump : jumps) {
			if(jump.getStart().getCharacterOffset() >= offset){
				break;
			}
			if(jump.isJump()){
				offset = offset - jump.getDiff();
				diff += jump.getDiff();
			}
		}
		offset -= this.getValueStartOffset();
		return offset;
	}
	
	public boolean isInJump(int offset){
		JumpRange jumpR = getJumpRange(offset);
		if(jumpR != null)
			return jumpR.isJump();
		return false;
	}
	
//	private int getValueStartOffset(){
//		return this.getValueStart().getCharacterOffset();
//	}
	
	private JumpRange getJumpRange(int offset){
		offset += getValueStartOffset();
		int diff = 0;
		for (JumpRange jump : this.jumps) {
			if(jumpMatch(jump, diff, offset)){
				return jump;
			}
			diff += jump.getDiff();
		}
		return null;
	}
	
	public JumpInfo getJumpInfo(int offset){
		JumpRange range = null;
		JumpInfo info = new JumpInfo();
		offset += this.getValueStartOffset();
		int diff = 0;
		for (JumpRange jump : this.jumps) {
			if(jumpMatch(jump, diff, offset)){
				range = jump;
				break;
			}
			diff += jump.getDiff();
		}
		
		
		info.startOffset = range.getStart().getCharacterOffset() - this.getValueStartOffset();
		info.endOffset = range.getEnd().getCharacterOffset() - this.getValueStartOffset();
		info.startNodeOffset = info.startOffset + diff;
		info.endNodeOffset = info.startNodeOffset + range.nodeLength;
		info.replacement = this.node.getNodeValue().substring(info.startNodeOffset, info.endNodeOffset);
		return info;
	}
	
	private boolean jumpMatch(JumpRange jump, int diff, int offset){
		return jump.getStart().getCharacterOffset() + diff < offset && jump.getEnd().getCharacterOffset() + diff + jump.getDiff() >= offset;
	}
	
	public class JumpInfo {
		public int startOffset;
		public int endOffset;
		
		public int startNodeOffset;
		public int endNodeOffset;
		
		public String replacement;
		
		@Override
		public String toString() {
			return "[" + startOffset + ":" + endOffset + "] " + replacement;
		}
	}
	
	@Override
	public NodeInfo nodeInfoWithOffset(int start, int end,
			LineColumnInfo lineColumns) {
		
		if(this.isInJump(start)){
			JumpInfo jumpInfo = this.getJumpInfo(start);
			
			start = jumpInfo.startOffset;
			
		} else {
			start = this.transformNodeToCode(start);
		}
		
		if(this.isInJump(end)){
			JumpInfo jumpInfo = this.getJumpInfo(end);
			
			end = jumpInfo.endOffset;
			
		} else {
			end = this.transformNodeToCode(end);
		}
		
		return super.nodeInfoWithOffset(start, end, lineColumns);
	}
	
}
