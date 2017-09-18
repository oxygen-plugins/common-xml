package com.github.oxygenPlugins.common.text;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.oxygenPlugins.common.text.StringUtil;

public class StringUtilTest {

	@Test
	public void testGetLinesArr() {
		assertArrayEquals(StringUtil.getLinesArr("textline 1\ntext line 2\n"), 
						  new String[]{"textline 1\n", "text line 2\n"});
	}

	@Test
	public void testEscapeRegex() {
		assertEquals(StringUtil.escapeRegex("50.00$"), "\\Q50.00$\\E");
	}

	@Test
	public void testMatches() {
		assertTrue(StringUtil.matches("this is some text", "\\ssome\\s"));
	}

}
