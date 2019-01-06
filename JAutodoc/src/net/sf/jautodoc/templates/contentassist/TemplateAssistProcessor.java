/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.contentassist;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;


/**
 * Template assist processor.
 */
public class TemplateAssistProcessor implements IContentAssistProcessor {
	private ITemplateContentAssistant[] assistants;
	
	
	/**
	 * Instantiates a new template assist processor.
	 * 
	 * @param assistants the assistants
	 */
	public TemplateAssistProcessor(ITemplateContentAssistant[] assistants) {
		this.assistants = assistants;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,	int documentOffset) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		IDocument document = viewer.getDocument();
		
		try {
			int pos = (documentOffset == document.getLength() ?
					documentOffset - 1 : documentOffset);
			IRegion region = document.getLineInformationOfOffset(pos);
			
			int startPosition = 0;
			ITemplateContentAssistant currentAssistant = null;
			for (int i = 0; i < assistants.length; ++i) {
				int start = assistants[i].getStartPosition(document, region, documentOffset);
				if (start > startPosition) {
					startPosition = start;
					currentAssistant = assistants[i];
				}
			}
			
			if (currentAssistant == null) {
				return new ICompletionProposal[0];
			}
			
			int length = documentOffset - startPosition;
			proposals  = currentAssistant.getCompletionProposals(document, startPosition, length);
		} catch (Exception e) {/* ignore */}		
		
		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		int length = 0;
		for (int i = 0; i < assistants.length; ++i) {
			length += assistants[i].getActivationCharacters().length;
		}
		
		int index = 0;
		char[] activationCharacters = new char[length];
		for (int i = 0; i < assistants.length; ++i) {
			char[] chars = assistants[i].getActivationCharacters();
			for (int j = 0; j < chars.length; ++j) {
				activationCharacters[index++] = chars[j];
			}
		}
		
		return activationCharacters;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer,int offset) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	public String getErrorMessage() {
		return null;
	}
}
