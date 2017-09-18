package com.github.oxygenPlugins.common.xml.xsd;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.xerces.parsers.StandardParserConfiguration;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;

@SuppressWarnings("unchecked")
public class DOMParser {
	private static Class<?> configClass;
	private static Class dompClass;
	private static Method setFeature;
	private static Method setProperty;
	private static Method setErrorHandler;
	private static Method parse;
	
	static {
		try {
			configClass = Class.forName("org.apache.xerces.parsers.StandardParserConfiguration");
			dompClass = Class.forName("org.apache.xerces.parsers.DOMParser");
			setFeature = dompClass.getMethod("setFeature", new Class[]{String.class, boolean.class});
			setProperty = dompClass.getMethod("setProperty", new Class[]{String.class, Object.class});
			setErrorHandler = dompClass.getMethod("setErrorHandler", new Class[]{org.xml.sax.ErrorHandler.class});
			parse = dompClass.getMethod("parse", new Class[]{InputSource.class});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private Object domparser = null;
	
	public static void main(String[] args) {
		DOMParser domp = new DOMParser();
		domp.setFeature("http://xml.org/sax/features/validation", true);
		domp.setFeature("http://apache.org/xml/features/validation/schema",
				true);
		domp.setFeature(
				"http://apache.org/xml/features/validation/schema-full-checking",
				true);
//		domp.setFeature("http://apache.org/xml/features/xinclude", true);
		domp.setFeature(
				"http://apache.org/xml/features/honour-all-schemaLocations",
				true);
	}
	
	protected DOMParser(){
			try {
				
				this.domparser = dompClass.getConstructor(org.apache.xerces.xni.parser.XMLParserConfiguration.class).newInstance(configClass.newInstance());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		
	}
	
	public void setFeature(String feature, boolean value){
		try {
			setFeature.invoke(domparser, feature, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	public void setProperty(String feature, Object value){
		try {
			setProperty.invoke(domparser, new Object[]{feature, value});
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	public void setErrorHandler(ErrorHandler handler){
		try {
			setErrorHandler.invoke(domparser, handler);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	public void parse(InputSource source){
		try {
			parse.invoke(domparser, source);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
