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

import net.sf.jautodoc.templates.contentassist.AbstractTemplateContentAssistant;
import net.sf.jautodoc.templates.velocity.rules.DirectiveDetector;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;


/**
 * Content assistant for Velocity directives.
 */
public class DirectiveContentAssistant extends AbstractTemplateContentAssistant {
	private DirectiveDetector directiveDetector = new DirectiveDetector();

	
	/**
	 * The Constructor.
	 */
	public DirectiveContentAssistant() {
		super(new String[] {"#"}, new char[] {'#'});
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.contentassist.AbstractTemplateContentAssistant#getCompletionProposals(org.eclipse.jface.text.IDocument, int, int)
	 */
	public List<ICompletionProposal> getCompletionProposals(IDocument document, int offset, int length) {
		try {
			String element = getElement(document, offset, length);
			if (element != null) {
				return DirectiveManager.getCompletionProposals(element, offset,	length);
			}
		} catch (Exception e) {/* ignore */}	
		
		return new ArrayList<ICompletionProposal>();
	}

	private String getElement(IDocument document, int offset, int length)
												throws BadLocationException {
		if (length <= 0 || offset > 0 &&
                document.getChar(offset - 1) == '\\') { // escaped directive
            return null;
        }

		String element = document.get(offset, length);
		if (element.startsWith("#") && isValidElement(element)) {
			return element;
		}
		return null;
	}
	
	private boolean isValidElement(String element) {
		char[] chars = element.toCharArray();
		if (chars.length == 0 || !directiveDetector.isWordStart(chars[0])) {
			return false;
		}
		
		for (int i = 1; i < chars.length; ++i) {
			if (!directiveDetector.isWordPart(chars[i])) return false;
		}
		
		return true;
	}
}
