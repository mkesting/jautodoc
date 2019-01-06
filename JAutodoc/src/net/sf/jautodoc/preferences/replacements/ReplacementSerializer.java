/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.replacements;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.utils.Utils;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import com.megginson.sax.DataWriter;

/**
 * Class for serializing the replacements.
 */
public class ReplacementSerializer {
	private static final String ROOT	= "replacements";
	private static final String ENTRY	= "replacement";
	private static final String KEY		= "key";
	private static final String SCOPE	= "scope";
	private static final String MODE	= "mode";
	
	private static boolean format = false;
	
	
	/**
	 * Serializes the replacements to the given file.
	 * 
	 * @param replacements the replacements
	 * @param file the file
	 * 
	 * @throws Exception an exception occured
	 */
	public static void serialize(Replacement[] replacements, File file) throws Exception {
		FileWriter fw = new FileWriter(file);
		format = true;
		try {
			serialize(replacements, fw);
		} finally {
			Utils.close(fw);
		}
	}
	
	/**
	 * Serialize the given replacements to string.
	 * 
	 * @param replacements the replacements
	 * 
	 * @return the string
	 */
	public static String serialize(Replacement[] replacements) {
		StringWriter sw = new StringWriter();
		format = false;
		try {
			serialize(replacements, sw);
		} catch (Exception e) {
			JAutodocPlugin.getDefault().handleException(e);
		}
		return sw.toString();
	}
	
	private static void serialize(Replacement[] replacements, Writer w) throws Exception {
		DataWriter writer = new DataWriter(w);
		
		writer.startDocument();
		writer.startElement(ROOT);
		
		writeReplacements(writer, replacements);
		
		writer.endElement(ROOT);
		writer.endDocument();
	}
	
	private static void writeReplacements(DataWriter writer,
			Replacement[] replacements) throws Exception {
		for (int i = 0; i < replacements.length; ++i) {
			writeReplacement(writer, replacements[i]);
		}
	}
	
	private static void writeReplacement(DataWriter writer,
			Replacement entry) throws Exception {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute("", KEY,	"", "", entry.getShortcut());
		attributes.addAttribute("", SCOPE,	"", "", Integer.toString(entry.getScope()));
		attributes.addAttribute("", MODE,	"", "", Integer.toString(entry.getMode()));
		
		if (format) {
			writer.setIndentStep(writer.getIndentStep() + 2);
		}
		
		writer.startElement("", ENTRY, "", attributes);
		writer.characters(entry.getReplacement());
		writer.endElement(ENTRY);
		
		if (format) {
			writer.setIndentStep(writer.getIndentStep() - 2);
		}
	}
	
	/**
	 * Deserialize replacements from the given file.
	 * 
	 * @param file the file
	 * 
	 * @return the replacements
	 * 
	 * @throws Exception an exception occured
	 */
	public static Replacement[] deserialize(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		try {
			return deserialize(fis);
		} finally {
			Utils.close(fis);
		}
	}

	/**
	 * Deserialize replacements from the given string.
	 * 
	 * @param s the string
	 * 
	 * @return the replacements
	 */
	public static Replacement[] deserialize(String s) {
		Replacement[] r = null;
		try {
			r = deserialize(new ByteArrayInputStream(s.getBytes()));
		} catch (Exception e) {
			JAutodocPlugin.getDefault().handleException(e);
		}
		return r != null ? r : new Replacement[0];
	}
	
	private static Replacement[] deserialize(InputStream is) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(false);

		XMLReader parser = factory.newSAXParser().getXMLReader();

		ContentHandler handler = new ContentHandler();
		parser.setContentHandler(handler);
		parser.setErrorHandler(handler);
		parser.parse(new InputSource(is));

		return handler.getReplacements();
	}
	
	private static class ContentHandler extends DefaultHandler {
		private StringBuffer buffer;
		private List<Replacement> replacements;
		private Replacement currentEntry;
		
		
		public Replacement[] getReplacements() {
			return replacements.toArray(new Replacement[replacements.size()]);
		}
		
		public void startDocument() throws SAXException {
			replacements = new ArrayList<Replacement>();
			currentEntry = null;
		}

		public void endDocument() throws SAXException {
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (ENTRY.equals(qName)) {
				currentEntry = new Replacement();
				currentEntry.setShortcut(attributes.getValue(KEY));
				currentEntry.setScope(Integer.parseInt(attributes.getValue(SCOPE)));
				currentEntry.setMode(Integer.parseInt(attributes.getValue(MODE)));
			}
			
			buffer = new StringBuffer();
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			if (buffer != null) {
				buffer.append(ch, start, length);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (ENTRY.equals(qName)) {
				currentEntry.setReplacement(buffer.toString());
				replacements.add(currentEntry);
			}
			
			buffer = null;
		}

		// ------------------------------------------------------------------------

		public void fatalError(SAXParseException e) throws SAXException {
			JAutodocPlugin.getDefault().handleException(e);
		}

		public void error(SAXParseException e) throws SAXException {
			JAutodocPlugin.getDefault().handleException(e);
		}

		public void warning(SAXParseException e) throws SAXException {
			JAutodocPlugin.getDefault().handleException(e);
		}
	}
}
