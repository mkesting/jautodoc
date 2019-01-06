/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.contentassist;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;


/**
 * Abstract base class for template content assistants.
 */
public abstract class AbstractTemplateContentAssistant implements
		ITemplateContentAssistant {

	private String[] startSequences;
	private char[] 	 activationCharacters;
	
	
	/**
	 * Instantiates a new abstract template content assistant.
	 * 
	 * @param startSequences the start sequences
	 * @param activationCharacters the activation characters
	 */
	public AbstractTemplateContentAssistant(
			String[] startSequences, char[] activationCharacters) {
		this.startSequences 	  = startSequences;
		this.activationCharacters = activationCharacters;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.contentassist.ITemplateContentAssistant#getCompletionProposals(org.eclipse.jface.text.IDocument, int, int)
	 */
	public abstract List<ICompletionProposal> getCompletionProposals(IDocument document, int offset, int length);
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.contentassist.ITemplateContentAssistant#getActivationCharacters()
	 */
	public char[] getActivationCharacters() {
		return activationCharacters;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.contentassist.ITemplateContentAssistant#getStartPosition(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.IRegion, int)
	 */
	public int getStartPosition(IDocument document, IRegion region, int offset)
													throws BadLocationException {
		if (offset <= region.getOffset() ||
				offset > region.getOffset() + region.getLength()) {
				return -1;
			}

		int startIndex = -1;
		int endIndex   = -1;

		String line = document.get(region.getOffset(), offset - region.getOffset());
		for (int i = 0; i < startSequences.length; ++i) {
			int index = line.lastIndexOf(startSequences[i]);
			if (index < 0) continue;

			if (startIndex < 0) {
				startIndex = index;
				endIndex   = startIndex + startSequences[i].length();
				continue;
			}

			// find the leftmost ending start sequence.
			// in case of equality get the one, that starts first
			int currentEndIndex = index + startSequences[i].length();
			if (currentEndIndex == endIndex && index < startIndex ||
					currentEndIndex > endIndex) {
				startIndex = index;
				endIndex   = startIndex + startSequences[i].length();
			}
		}

		return startIndex >= 0 ? region.getOffset() + startIndex : -1;
	}
}
