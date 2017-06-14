package com.github.oxygenPlugins.common.xml.staxParser;

import javax.xml.stream.Location;

// !!!IMPORTANT!!!
// NEEDS TO BE INTEGRATED INTO WOODSTOX PARSER (com.ctc.wstx.sr.AttributeListener)

public interface AttributeListener {

	public abstract void attributeNameStart(Location loc);

	public abstract void attributeNameEnd(Location loc, String prefix,
			String localName);

	public abstract void attributeValueStart(Location loc);

	public abstract void attributeEntityStart(Location loc);

	public abstract void attributeEntityEnd(Location loc,
			Integer valueLength);

	public abstract void attributeValueEnd(Location loc, String value);

	public abstract void attributeRegionStart(Location currentLocation);

	public abstract void attributeRegionEnd(Location currentLocation);
}
