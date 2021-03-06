package com.github.oxygenPlugins.common.text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLResolver;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.mozilla.universalchardet.UniversalDetector;
import org.xml.sax.InputSource;

import com.github.oxygenPlugins.common.text.uri.DefaultURIResolver;
import com.github.oxygenPlugins.common.xml.xslt.SaxonUtils;


public class TextSource {
	private static char BOMCHAR = '\ufeff';
	private static String BOMSTRING = new String(new char[]{BOMCHAR});
	
	public static String WINDOWS_LINEFEED_FORMAT = "\r\n";
	public static String UNIX_LINEFEED_FORMAT = "\n";
	public static String OSX_LINEFEED_FORMAT = "\r";
	
	private String encoding = DEFAULT_ENCODING;
	private boolean hasBOM = false;
	private String data = "";
	private File file;
	
	private String lineSeperator = "\n";
	
	public String getEncoding(){
		return this.encoding;
	}
	public String toString(){
		return this.data;
	}
	public String toString(String seperatorFormat){
		if(seperatorFormat.equals(this.lineSeperator)){
			return this.data;
		} else {
			return this.data.replaceAll(this.lineSeperator, seperatorFormat);
		}
	}
	
	public boolean hasBOM(){
		return this.hasBOM;
	}
	public File getFile(){
		return this.file;
	}
	
	private TextSource(File f){
		this.file = f;
	}
	private TextSource(File f, String encoding){
		this(f);
		this.setEncoding(encoding);
	}
	
	private void setEncoding(String enc){
		this.encoding = enc;
	}
	public void udateData(String data){
		updateData(data, detectLineSeperator(data));
	}
	public void updateData(String data){
		String lineSepFormat = detectLineSeperator(data);
		updateData(data, lineSepFormat);
	}
	public void updateData(String data, String lineSepFormat){
		if(lineSepFormat.equals(this.lineSeperator)){
			this.data = data;
		} else {
			this.data = data.replaceAll(lineSepFormat, this.lineSeperator);
		}
	}
	
	public TextSource setData(String data){
		this.data = data;
		this.lineSeperator = detectLineSeperator(data);
		return this;
	}
	private void setHasBOM(boolean hasBOM){
		this.hasBOM = hasBOM;
	}
	
	private String detectLineSeperator(String data){
		String lineSep = System.getProperty("line.separator");
		boolean win = StringUtil.matches(data, "\\r\\n");
		boolean unix = StringUtil.matches(data, "[^\\r]\\n");
		boolean osx = StringUtil.matches(data, "\\r[^\\n]");
		if(win && !(unix || osx)){
			lineSep = WINDOWS_LINEFEED_FORMAT;
		} else if (unix && !(win || osx)){
			lineSep = UNIX_LINEFEED_FORMAT;
		} else if (osx && !(win || unix)){
			lineSep = OSX_LINEFEED_FORMAT;
		}
		return lineSep;
	}
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	public void write() throws IOException{
		TextSource.write(this.getFile(), this);
	}
	
	public TextSource copy() {
		TextSource copy = new TextSource(this.file);
		copy.data = this.data;
		copy.encoding = this.encoding;
		copy.hasBOM = this.hasBOM;
		copy.file = this.file;
		copy.lineSeperator = this.lineSeperator;
		return copy;
	}
	public String getLineSeperator() {
		return this.lineSeperator;
	}
	
	public void convertLineSeperator(String newLineSep){
		this.data = this.toString(newLineSep);
		this.lineSeperator = newLineSep;
	}
	
	public static TextSource createVirtualTextSource(File file){
		return createVirtualTextSource(file, DEFAULT_ENCODING);
	}
	
	public static TextSource createVirtualTextSource(File file, String encoding){
		return new TextSource(file, encoding);
	}

	public static void write(File outputFile, TextSource doc) throws IOException {
		write(outputFile, doc, doc.getEncoding());
	}
	public static void write(File outputFile, TextSource doc, String encoding) throws IOException {
		String bom = doc.hasBOM() ? BOMSTRING : "";
		String docString = bom + doc.toString();
		
		FileOutputStream fileStream = new FileOutputStream(outputFile);
		Writer out = new OutputStreamWriter(fileStream, encoding);
		out.write(docString);
		out.close();
		return;
	}
	private static String detectEncodingUD(File input){
		try {
			return detectEncodingUD(new BufferedInputStream(new java.io.FileInputStream(input)));
		} catch (FileNotFoundException e) {
			return DEFAULT_ENCODING;
		}
	}
	public static String detectEncodingUD(BufferedInputStream input){
	    try {
	    	input.mark(5);
	    	byte[] buf = new byte[4096];
	    	int nread;

	    	UniversalDetector detector = new UniversalDetector(null);
			
	    	while ((nread = input.read(buf)) > 0 && !detector.isDone()) {
			  detector.handleData(buf, 0, nread);
			}
			
	    	detector.dataEnd();

			String encoding = detector.getDetectedCharset();
			
			input.reset();
			if(encoding == null){
				return detectEncoding(input);
			} else {
				return encoding;
			}
		} catch (IOException e) {
			try {
				input.reset();
			} catch (IOException e1) {
				return DEFAULT_ENCODING;
			}
			return DEFAULT_ENCODING;
		}
	    // (3)

	    // (4)
	}
	@SuppressWarnings("unused")
	private static String detectEncoding(File input) {
		try {
			return detectEncoding(new BufferedInputStream(new FileInputStream(input)));
		} catch (FileNotFoundException e) {
			return DEFAULT_ENCODING;
		}
	}
	private static String detectEncoding(BufferedInputStream input) {
		try {
			input.mark(5);
			
			InputStreamReader isr = new InputStreamReader(input, DEFAULT_ENCODING);
			BufferedReader breader = new BufferedReader(isr);
			char[] buf = new char[64];
			int numRead = 0;
			numRead = breader.read(buf);
			String readData = new String(buf, 0, numRead);
			if (readData.startsWith("<?xml")) {
				while (!readData.contains("?>")) {
					numRead = breader.read(buf);
					readData = readData + new String(buf, 0, numRead);
					if(numRead == -1){
						break;
					}

				}
				input.reset();
				
				String xmlDecl = readData.substring(0,
						readData.indexOf("?>") + 2);
				Pattern pattern = Pattern
						.compile("\\s+encoding\\s*=\\s*(\'|\")(.*?)\\1");
				Matcher matcher = pattern.matcher(xmlDecl);
				if (matcher.find()) {
					String enc = matcher.group(2);
					return enc;
				} else {
					return DEFAULT_ENCODING;
				}
			} else {
				input.reset();
				return DEFAULT_ENCODING;
			}
		} catch (Exception e) {
			try {
				input.reset();
			} catch (IOException e1) {
				return DEFAULT_ENCODING;
			}
			return DEFAULT_ENCODING;
		}
	}
	
	public static TextSource readTextFile(Source src) throws IOException {
		TextSource ts = new TextSource(new File(src.getSystemId()));
		Reader reader = getReader(src);
		if(reader == null)
			throw new IOException("Source is not readable");
		return readTextFile(reader, ts);
	}
	
	public static TextSource readTextFile(File input) throws IOException {
		String encoding =  detectEncodingUD(input);
		TextSource ts = new TextSource(input, encoding);
		Reader reader = resolveFile(input, encoding);
		return readTextFile(reader, ts);
	}
	
	public static TextSource readTextFile(File input, String encoding)
			throws FileNotFoundException {
		TextSource tr = new TextSource(input);
		tr.setEncoding(encoding);
		StringBuilder text = new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = new Scanner(new FileInputStream(input), encoding);
		try {
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine() + NL);
			}
		} finally {
			scanner.close();
		}
		tr.setData(text.toString());
		return tr;
	}
	
	public static void implementResolver(DefaultURIResolver resolver){
		TextSource.resolver = resolver; 
	}
	public static void implementEntityResolver(XMLResolver resolver){
		TextSource.entityResolver = resolver; 
	}
	public static XMLResolver getEntityResolver(){
		return entityResolver;
	}
	
	public static void resetResolver(){
		resolver = null;
	}

	private static DefaultURIResolver resolver = null;
	private static XMLResolver entityResolver = null;
	
	private static Reader resolveFile(File input, String encoding) throws IOException {
		if(resolver != null){
			Source src;
			try {
				src = resolver.resolve(input.toURI().toString(), new File(".").toURI().toString());
				if(src instanceof StreamSource){
					StreamSource ssrc = (StreamSource) src;
					Reader reader = ssrc.getReader();
					if(reader == null){
						reader = new InputStreamReader(ssrc.getInputStream());
					}
					return reader;
				} else if (src instanceof InputSource) {
					InputSource isrc = (InputSource) src;
					return isrc.getCharacterStream();
				}
			} catch (TransformerException e) {}
		}
		return new InputStreamReader(new FileInputStream(input), encoding);
	}
	
	private static Reader resolveFile(URL url, String encoding){
		if(resolver != null){
			Source src;
			try {
				src = resolver.resolve(url.toURI().toString(), new File(".").toURI().toString());
				if(src instanceof StreamSource){
					StreamSource ssrc = (StreamSource) src;
					Reader reader = ssrc.getReader();
					if(reader == null){
						reader = new InputStreamReader(ssrc.getInputStream());
					}
					return reader;
				} else if (src instanceof InputSource) {
					InputSource isrc = (InputSource) src;
					return isrc.getCharacterStream();
				}
			} catch (TransformerException e) {} catch (URISyntaxException e) {
			}
		}
		try {
			return new InputStreamReader(url.openStream(), encoding);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static Reader getReader(Source src){
		if(src instanceof StreamSource){
			StreamSource ssrc = (StreamSource) src;
			Reader reader = ssrc.getReader();
			if(reader == null){
				InputStream is = ssrc.getInputStream();
				if( is != null){
					reader = new InputStreamReader(is);
				}
			}
			return reader;
		} else if (src instanceof InputSource) {
			InputSource isrc = (InputSource) src;
			return isrc.getCharacterStream();
		}
		return null;
	}
	
	public static TextSource readTextFile(File input, String encoding,
			boolean byLine) throws IOException {
		if (byLine) {
			return readTextFile(input, encoding);
		} else {
			TextSource tr = new TextSource(input, encoding);
			Reader reader = resolveFile(input, encoding);
			return readTextFile(reader, tr);
		}
	}
	

	public static TextSource readTextFile(URL url) throws IOException {
		TextSource tr = new TextSource(new File(url.getFile()));
		return readTextFile(resolveFile(url, DEFAULT_ENCODING), tr);
	}

	private static TextSource readTextFile(Reader reader, TextSource tr) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader breader = new BufferedReader(reader);
		char[] buf = new char[1024];
		int numRead = 0;
		boolean isFirst = true;
		while ((numRead = breader.read(buf)) != -1) {
			boolean isBom = buf[0] == BOMCHAR;
			tr.setHasBOM(isBom);
			int bomCorr = (isBom && isFirst) ? 1 : 0;
			String readData = new String(buf, bomCorr, numRead - bomCorr);
			isFirst = false;
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		String documentText = new String(fileData);
		tr.setData(documentText);
		return tr;

	}


	public static TextSource readTextFile(File input, boolean byLine)
			throws IOException {
		return readTextFile(input, detectEncodingUD(input), byLine);
	}
	public static TextSource readTextFile(Reader reader, String encoding, File file) throws IOException {
		TextSource tr = new TextSource(file, encoding);
		return readTextFile(reader, tr);
	}
	public static TextSource readTextFile(InputStream stream, String encoding, File file) throws IOException {
		// TODO Auto-generated method stub
		InputStreamReader reader = new InputStreamReader(stream, encoding);
		return readTextFile(reader, encoding, file);
	}
	
	public static TextSource readTextFile(InputStream stream, File file) throws IOException {
		BufferedInputStream bstream = new BufferedInputStream(stream);
		String encoding = detectEncodingUD(bstream);
		return readTextFile(bstream, encoding, file);
	}
	
	public static TextSource readTextFile(InputStream stream) throws IOException {
		return readTextFile(stream, File.createTempFile("escali", null));
	}
	
	public static TextSource readXmlFile(Source source) throws IOException {
		// TODO Auto-generated method stub
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			throw new IOException();
		} catch (TransformerException e) {
			throw new IOException();
		}
		URI uri;
		try {
			uri = new URI(source.getSystemId());
		} catch (URISyntaxException e) {
			throw new IOException();
		}
		File file;
		if(uri.getScheme().equals("jar")){
			URL url = uri.toURL();
			JarURLConnection connection = (JarURLConnection) url.openConnection();
			try {
				file = new File(connection.getJarFileURL().toURI());
			} catch (URISyntaxException e) {
				throw new IOException();
			}
		} else {
			file = new File(uri);
		}
		TextSource resultSource = TextSource.createVirtualTextSource(file);
		resultSource.setData(writer.toString());
		
		return resultSource;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}
	public static boolean hasResolver() {
		return resolver != null;
	}
	public static DefaultURIResolver getResolver() {
		return resolver;
	}
	public Source createSource() {
		return SaxonUtils.getStreamSource(this);
	}

}
