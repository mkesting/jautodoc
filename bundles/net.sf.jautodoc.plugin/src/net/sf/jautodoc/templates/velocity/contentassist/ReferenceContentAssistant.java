/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.contentassist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.templates.contentassist.AbstractTemplateContentAssistant;
import net.sf.jautodoc.templates.rules.IRulesStrategy;
import net.sf.jautodoc.templates.rules.TemplateCodeScanner;
import net.sf.jautodoc.templates.velocity.rules.StartingReferenceRulesStrategie;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;

/**
 * Content assistant for Velocity references.
 */
public class ReferenceContentAssistant extends AbstractTemplateContentAssistant {
	private Map<String, String> properties;
	private boolean propertiesOnly;
	private ITokenScanner referenceScanner;
	
	
	/**
	 * Instantiates a new reference content assistant.
	 * 
	 * @param propertiesOnly true, if properties only should be suggested
	 * @param properties the properties
	 */
	public ReferenceContentAssistant(Map<String, String> properties, boolean propertiesOnly) {
		super(new String[] {"$"}, propertiesOnly ? new char[] {'$'} : new char[] {'$', '.'});
		
		this.properties = properties;
		this.propertiesOnly = propertiesOnly;
		
		IRulesStrategy[] ruleStrategies = {
				new StartingReferenceRulesStrategie()};
		referenceScanner = new TemplateCodeScanner(ruleStrategies);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.contentassist.AbstractTemplateContentAssistant#getCompletionProposals(org.eclipse.jface.text.IDocument, int, int)
	 */
	public List<ICompletionProposal> getCompletionProposals(IDocument document, int offset, int length) {
		try {
			String element = getElement(document, offset, length);
			if (element != null) {
				return ReferenceManager.getCompletionProposals(element, offset,	length, properties, propertiesOnly);
			}
		} catch (Exception e) {/* ignore */}	
		
		return new ArrayList<ICompletionProposal>();
	}

	private String getElement(IDocument document, int offset, int length)
												throws BadLocationException {
		if (length <= 0 || offset > 0 &&
				document.getChar(offset - 1) == '\\') { // escaped reference
			return null;
		}
		
		String element = document.get(offset, length);
		if (element.startsWith("$") && isValidReference(document, offset, length)) {
			return element;
		}
		return null;
	}
	
	private boolean isValidReference(IDocument document, int offset, int length) {
		referenceScanner.setRange(document, offset, length);
		
		IToken token = referenceScanner.nextToken();
		
		return  referenceScanner.getTokenOffset() == offset &&
				referenceScanner.getTokenLength() == length &&
				token.equals(ResourceManager.getToken(ResourceManager.REFERENCE));
	}
}
