package com.github.oxygenPlugins.common.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.github.oxygenPlugins.common.text.uri.DefaultURIResolver;

public class TextSourceTest {

	@Test
	public void testSpecCharfromURL() {
		try {
			URL url = new File("src/test/resources/com/github/oxygenPlugins/common/specialChars/specialChar.xml").toURI().toURL();
			
			TextSource src = TextSource.readTextFile(url);
			assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<schema><report>Whitespace in Produktnamen sollten geschützt sein (&amp;#xA0;)!</report></schema>", src.toString());
			
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testSpecCharURLwDefResolver() {
		try {
			
			TextSource.implementResolver(new DefaultURIResolver());
			
			URL url = new File("src/test/resources/com/github/oxygenPlugins/common/specialChars/specialChar.xml").toURI().toURL();
			
			TextSource src = TextSource.readTextFile(url);
			assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
					+ "<schema><report>Whitespace in Produktnamen sollten geschützt sein (&amp;#xA0;)!</report></schema>", src.toString());
			
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}


}
