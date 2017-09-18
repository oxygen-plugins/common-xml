package com.github.oxygenPlugins.common.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Janos
 * @version 30.08.2011 | 01:05:38
 * 
 */
public class RegexUtil {
	
	public static final boolean startsWith(final String text, final String regex) {
		if (text == null || regex == null) {
			return false;
		}
		final Pattern p = Pattern.compile(regex);
		final Matcher m = p.matcher(text);
		return m.find() && m.start() == 0;
	}
	
	public static Pattern wildcardToRegex(String wildcard){
		Pattern wildcardRgx = Pattern.compile("[^*;]+|(\\*)|(;)");
		Matcher m = wildcardRgx.matcher(wildcard);
		StringBuffer b= new StringBuffer();
		while (m.find()) {
		    if(m.group(1) != null) m.appendReplacement(b, ".*");
		    else if(m.group(2) != null) m.appendReplacement(b, ")|(");
		    else m.appendReplacement(b, "\\\\Q" + m.group(0) + "\\\\E");
		}
		m.appendTail(b);
		String regex = "^((" + b.toString() + "))$";
		return Pattern.compile(regex);
	}
	
	public static void main(String[] args) {
		boolean startsWith = startsWith("UFC 123 - blablabla", "UFC \\d\\d\\d");
		System.err.println(startsWith);
	}
	

}
