/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * Template proposal class.
 */
public class TemplateProposal implements ICompletionProposal {

	private String 	element;
	private String 	displayString;
	private int 	offset;
	private int 	length;
	private Point 	selection;

	/**
	 * Creates a template element proposal.
	 * 
	 * @param displayString the display string
	 * @param element the element
	 * @param length the length to replace
	 * @param offset the offset to replace
	 * @param selection the selection
	 */
	public TemplateProposal(String element, String displayString,
										int offset, int length, Point selection) {
		this.element 		= element;
		this.displayString 	= displayString;
		this.offset			= offset;
		this.length			= length;
		this.selection		= selection;
	}

	/*
	 * @see ICompletionProposal#apply(IDocument)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document) {

		try {
			int idx = offset + length; 
			if (idx < document.getLength() &&
					(element.endsWith("}") && document.getChar(idx) == '}' ||
					 element.endsWith(")") && document.getChar(idx) == ')')) {
				++length;
			}
			
			document.replace(offset, length, element);
		} catch (BadLocationException e) {/* ignore */}
	}

	/*
	 * @see ICompletionProposal#getDisplayString()
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		return displayString;
	}
	
	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document) {
		return selection;
	}

	/*
	 * @see ICompletionProposal#getAdditionalProposalInfo()
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return null;
	}

	/*
	 * @see ICompletionProposal#getImage()
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return null;
	}

	/*
	 * @see ICompletionProposal#getContextInformation()
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return null;
	}
}
