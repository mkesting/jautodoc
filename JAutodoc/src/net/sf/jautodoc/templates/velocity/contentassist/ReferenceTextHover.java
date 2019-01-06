/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.contentassist;

import java.util.Map;

import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.templates.rules.IRulesStrategy;
import net.sf.jautodoc.templates.rules.TemplateCodeScanner;
import net.sf.jautodoc.templates.velocity.rules.StartingReferenceRulesStrategie;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;


/**
 * Text hover for Velocity references.
 */
public class ReferenceTextHover  implements ITextHover {
	private Map<String, String> properties;
	private ITokenScanner referenceScanner;
	
	
	/**
	 * Instantiates a new reference text hover.
	 */
	public ReferenceTextHover(Map<String, String> properties) {
		IRulesStrategy[] rulesStrategies = {
				new StartingReferenceRulesStrategie()};
		referenceScanner = new TemplateCodeScanner(rulesStrategies);
		
		this.properties = properties;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String info = null;
		try {
			info = ReferenceManager.getElementDescription(textViewer.getDocument()
					.get(hoverRegion.getOffset(), hoverRegion.getLength()), properties);
		} catch (Exception e) {/* ignore */}
		
		return info;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		IRegion region = null; 
		try {
			region = getElementRegion(textViewer.getDocument(), offset);
		} catch (Exception e) {/* ignore */}
		
		return region;
	}
	
	private IRegion getElementRegion(IDocument document, int offset) throws BadLocationException {
		if (offset == document.getLength()) {
			return null;
		}
		
		// get start of line
		IRegion lineInfo = document.getLineInformationOfOffset(offset);
		int lineOffset = lineInfo.getOffset();

		// find start of reference
		int referenceOffset = offset;
		for (; lineOffset <= referenceOffset; --referenceOffset) {
			if (document.getChar(referenceOffset) == '$') break;
		}
		if (referenceOffset < lineOffset) return null;

		// find reference
		int length = lineInfo.getLength() - (referenceOffset - lineOffset);
		referenceScanner.setRange(document, referenceOffset, length);
		IToken token = referenceScanner.nextToken();
		if (!token.equals(ResourceManager.getToken(ResourceManager.REFERENCE))) {
			return null; // invalid reference
		}

		// extract element / function
		int elementStart = getElementStart(document, offset);
		int elementEnd   = getElementEnd(document, offset);
		if (elementStart < elementEnd) {
			return new Region(elementStart, elementEnd - elementStart);
		}

		return null;
	}
	
	private int getElementStart(IDocument document, int offset) throws BadLocationException {
		int elementStart = offset;
		for (; referenceScanner.getTokenOffset() <= elementStart; --elementStart) {
			char ch = document.getChar(elementStart);
			if (ch == '$' || ch == '!' || ch == '{' || ch == '.') {
				++elementStart;
				break;
			}
		}
		return elementStart;
	}
	
	private int getElementEnd(IDocument document, int offset) throws BadLocationException {
		int elementEnd = offset;
		int referenceEnd = referenceScanner.getTokenOffset() + referenceScanner.getTokenLength();
		for (; elementEnd < referenceEnd; ++elementEnd) {
			char ch = document.getChar(elementEnd);
			if (ch == '!' || ch == '{' || ch == '}' || ch == '.') {
				break;
			}
		}
		return elementEnd;
	}
}
