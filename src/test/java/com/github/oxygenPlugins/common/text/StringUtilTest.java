package com.github.oxygenPlugins.common.text;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.oxygenPlugins.common.text.StringUtil;

public class StringUtilTest {

	@Test
	public void testGetLinesArr() {
	}

	@Test
	public void testEscapeRegex() {
	}

	@Test
	public void testMatches() {
		assertTrue(StringUtil.matches("this is some text", "\\ssome\\s"));
	}

}
