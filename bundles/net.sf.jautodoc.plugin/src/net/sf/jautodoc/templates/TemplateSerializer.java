/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Constants;
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
 * Class for serializing the templates.
 */
public class TemplateSerializer implements Constants {
	private static final String TEMPLATE_ROOT 		= "templates";
	private static final String TEMPLATE_ENTRY 		= "template";
	private static final String TEMPLATE_NAME 		= "name";
	private static final String TEMPLATE_KIND 		= "kind";
	private static final String TEMPLATE_SIGNATURE	= "signature";
	private static final String TEMPLATE_DEFAULT	= "default";
	private static final String TEMPLATE_REGEX 		= "regex";
	private static final String TEMPLATE_TEXT 		= "text";
	private static final String TEMPLATE_EXAMPLE	= "example";
	
	
	/**
	 * Load templates from file.
	 * 
	 * @param file the file
	 * 
	 * @return the template set
	 * 
	 * @throws Exception thrown if an exception occured
	 */
	public static TemplateSet loadTemplates(File file) throws Exception {
		return loadTemplates(new FileInputStream(file));
	}
	
	/**
	 * Load templates from input stream.
	 * 
	 * @param is the input stream
	 * 
	 * @return the template set
	 * 
	 * @throws Exception thrown if an exception occured
	 */
	public static TemplateSet loadTemplates(InputStream is) throws Exception {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(false);

			XMLReader parser = factory.newSAXParser().getXMLReader();
			
			ContentHandler handler = new ContentHandler();
			parser.setContentHandler(handler);
			parser.setErrorHandler(handler);
			parser.parse(new InputSource(is));
			
			return handler.getTemplates();
		} finally {
			Utils.close(is);
		}
	}
	
	/**
	 * Store templates to the given file.
	 * 
	 * @param templates the templates
	 * @param file the file
	 * 
	 * @throws Exception thrown if an exception occured
	 */
	public static void storeTemplates(TemplateSet templates, File file) throws Exception {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			DataWriter writer = new DataWriter(fw);
			
			writer.startDocument();
			writer.startElement(TEMPLATE_ROOT);
			
			writeTemplates(writer, templates);
			
			writer.endElement(TEMPLATE_ROOT);
			writer.endDocument();
		} finally {
			Utils.close(fw);
		}
	}
	
	private static void writeTemplates(DataWriter writer, TemplateSet templates) throws Exception {
		List<TemplateEntry> list = templates.getTypeTemplates();
		for (int i = 0; i < list.size(); ++i) {
			writeTemplateEntry(writer, (TemplateEntry)list.get(i));
		}
		
		list = templates.getFieldTemplates();
		for (int i = 0; i < list.size(); ++i) {
			writeTemplateEntry(writer, (TemplateEntry)list.get(i));
		}
		
		list = templates.getMethodTemplates();
		for (int i = 0; i < list.size(); ++i) {
			writeTemplateEntry(writer, (TemplateEntry)list.get(i));
		}
		
		list = templates.getParameterTemplates();
		for (int i = 0; i < list.size(); ++i) {
			writeTemplateEntry(writer, (TemplateEntry)list.get(i));
		}
		
		list = templates.getExceptionTemplates();
		for (int i = 0; i < list.size(); ++i) {
			writeTemplateEntry(writer, (TemplateEntry)list.get(i));
		}
	}
	
	private static void writeTemplateEntry(DataWriter writer, TemplateEntry entry) throws Exception {
		AttributesImpl attributes = new AttributesImpl();
		attributes.addAttribute("", TEMPLATE_KIND, 	    "", "", Integer.toString(entry.getKind()));
		attributes.addAttribute("", TEMPLATE_NAME, 	    "", "", entry.getName());
		attributes.addAttribute("", TEMPLATE_DEFAULT,   "", "", Boolean.toString(entry.isDefaultTemplate()));
		attributes.addAttribute("", TEMPLATE_SIGNATURE, "", "", Boolean.toString(entry.isUseSignature()));
		
		writer.startElement("", TEMPLATE_ENTRY, "", attributes);
		writer.setIndentStep(writer.getIndentStep() + 2);
		
		writer.startElement(TEMPLATE_REGEX);
		writer.characters(entry.getRegex());
		writer.endElement(TEMPLATE_REGEX);

		writer.startElement(TEMPLATE_EXAMPLE);
		writer.characters(entry.getExample());
		writer.endElement(TEMPLATE_EXAMPLE);
		
		writer.startElement(TEMPLATE_TEXT);
		writer.characters(entry.getText());
		writer.endElement(TEMPLATE_TEXT);
		
		writeTemplates(writer, entry.getChildTemplates());
		
		writer.setIndentStep(writer.getIndentStep() - 2);
		writer.endElement(TEMPLATE_ENTRY);
	}
	

	private static class ContentHandler extends DefaultHandler {
		private StringBuffer buffer;
		private TemplateSet templates;
		private TemplateEntry currentEntry;
		
		
		public TemplateSet getTemplates() {
			return templates;
		}
		
		public void startDocument() throws SAXException {
			templates = new TemplateSet();
			currentEntry = null;
		}

		public void endDocument() throws SAXException {
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (TEMPLATE_ENTRY.equals(qName)) {
				currentEntry = new TemplateEntry(currentEntry);
				currentEntry.setKind(Integer.parseInt(attributes.getValue(TEMPLATE_KIND)));
				currentEntry.setName(attributes.getValue(TEMPLATE_NAME));
				currentEntry.setDefaultTemplate(Boolean.valueOf(attributes.getValue(TEMPLATE_DEFAULT)).booleanValue());
				currentEntry.setUseSignature(Boolean.valueOf(attributes.getValue(TEMPLATE_SIGNATURE)).booleanValue());
			}
			
			buffer = new StringBuffer();
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			if (buffer != null) {
				buffer.append(ch, start, length);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (TEMPLATE_ENTRY.equals(qName)) {
				if (currentEntry.getParent() == null) {
					templates.addTemplate(currentEntry);
				}
				else {
					currentEntry.getParent().addChildTemplate(currentEntry);
				}
				currentEntry = currentEntry.getParent();
			}
			else if (TEMPLATE_REGEX.equals(qName)) {
				currentEntry.setRegex(buffer.toString());
			}
			else if (TEMPLATE_EXAMPLE.equals(qName)) {
				currentEntry.setExample(buffer.toString());
			}
			else if (TEMPLATE_TEXT.equals(qName)) {
				currentEntry.setText(buffer.toString());
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
