/*******************************************************************
 * Copyright (c) 2006 - 2025, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.contentassist;

import net.sf.jautodoc.templates.rules.ITemplatePartitions;
import net.sf.jautodoc.templates.velocity.rules.DirectiveDetector;
import net.sf.jautodoc.utils.Utils;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;

/**
 * Strategy for auto indention in Velocity templates.
 */
public class AutoIndentStrategy implements IAutoEditStrategy {

	private DirectiveDetector directiveDetector = new DirectiveDetector();


	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IAutoEditStrategy#customizeDocumentCommand(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.DocumentCommand)
	 */
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		if (d.getLength() == 0 ||
				c.offset == -1 || c.length > 0 || c.text == null) {
			return;
		}

		String[] lineDelimiters = d.getLegalLineDelimiters();
		int index = TextUtilities.endsWith(lineDelimiters, c.text);
		if (index > -1 && lineDelimiters[index].equals(c.text)) {
			indentAfterNewLine(d, c);
		}
	}

	private void indentAfterNewLine(IDocument d, DocumentCommand c) {
		try {
			String partition = Utils.getPartition(d, c.offset);

			// find start of line
			IRegion info = d.getLineInformation(d.getLineOfOffset(c.offset));
			int lineOffset = info.getOffset();
			int lineLength = info.getLength();

			// line empty or comment closed -> do nothing
			String line = d.get(lineOffset, lineLength).trim();
			if (line.length() == 0 || line.endsWith("*/")) {
				return;
			}

			// find white spaces
			int eow = findEndOfWhiteSpace(d, lineOffset, c.offset);

			String indention = "";
			StringBuffer buf = new StringBuffer(c.text);
			if (eow > lineOffset) {
				indention = d.get(lineOffset, eow - lineOffset);
				buf.append(indention);
			}

			// inside brackets or quotes -> no prefix
			if (isInsideBrackets(d, lineOffset, c.offset)) {
				return;
			}

			// check type of partition
			if (partition.equals(ITemplatePartitions.MULTI_LINE_COMMENT)) {
				// Velocity multi line comment partition
				handleMultiLineCommentPartition(d, eow, buf);
			}
			else if (partition.equals(ITemplatePartitions.SINGLE_LINE_COMMENT)) {
				// Velocity single line comment partition
				handleSingleLineCommentPartition(d, indention, buf);
			}
			else {
				// default partition (javadoc + Velocity directives)
				handleDefaultPartition(d, c, info, eow, indention, buf);
			}

			c.text = buf.toString();
		} catch (BadLocationException excp) {/* stop work */}
	}

	private void handleMultiLineCommentPartition(IDocument d, int eow,
			StringBuffer buf) throws BadLocationException {
		if (d.getChar(eow) == '#') {
			buf.append(" * "); // first line after start of comment
		}
		else {
			buf.append("* "); // indention already set
		}
	}

	private void handleSingleLineCommentPartition(IDocument d, String indention,
			StringBuffer buf) throws BadLocationException {
		if (indention.length() == 0) {
			buf.append(" * "); // first line after comment
		}
		else {
			buf.append("* "); // indention already set
		}
	}

	private void handleDefaultPartition(IDocument d, DocumentCommand c,
			IRegion lineInfo, int eow, String indention, StringBuffer buf)
			throws BadLocationException {
		char ch = d.getChar(eow);
		if (ch == '/' && eow + 1 < d.getLength() &&
				(d.getChar(eow + 1) == '*' || d.getChar(eow + 1) == '/')) {
			// start of Java multi/single line comment
			if (d.getChar(eow + 1) == '*') {
				// multi line comment
				buf.append(" * ");
				c.caretOffset = c.offset + buf.length();
				c.shiftsCaret = false;
				buf.append(d.getLegalLineDelimiters()[0] + indention + " */");
			}
			else if (eow + 2 < d.getLength() && d.getChar(eow + 2) == '/') {
			    // Markdown comment
                buf.append("/// ");
			}
			else {
				// single line comment
				buf.append("// ");
			}
		}
		else if (ch == '#' &&
				eow + 1 < d.getLength() && d.getChar(eow + 1) == '*') {
			// start of Velocity multi line comment
			buf.append(" * "); // start of comment
			c.caretOffset = c.offset + buf.length();
			c.shiftsCaret = false;
			buf.append(d.getLegalLineDelimiters()[0] + indention + " *#");
		}
		else if (directiveDetector.isWordStart(ch)) {
			// directive
			int directiveEnd = eow + 1;
			while (directiveEnd < lineInfo.getOffset() + lineInfo.getLength() &&
					directiveDetector.isWordPart(d.getChar(directiveEnd))) {
				++directiveEnd;
			}

			String directive = d.get(eow, directiveEnd - eow);
			if (DirectiveManager.isMultiLineDirective(directive)) {
				buf.append(" * ");
				c.caretOffset = c.offset + buf.length();
				c.shiftsCaret = false;
				buf.append(d.getLegalLineDelimiters()[0] + indention + "#end");
			}
			else {
				buf.append(" * "); // first line after directive
			}
		}
		else if (ch != '\r' && ch != '\n' && ch != '<' && !Character.isLetterOrDigit(ch)){
			buf.append(ch).append(" "); // char from previous line
		}
	}

	private int findEndOfWhiteSpace(IDocument document, int offset, int end)
			throws BadLocationException {
		while (offset < end) {
			char c = document.getChar(offset);
			if (c != ' ' && c != '\t') {
				return offset;
			}
			++offset;
		}
		return end;
	}

	private boolean isInsideBrackets(IDocument d, int start, int end)
			throws BadLocationException {
		int cntLeftParenthesis  = 0;
		int cntRightParenthesis = 0;
		int cntLeftBrace 		= 0;
		int cntRightBrace 		= 0;
		int cntLeftBracket 		= 0;
		int cntRightBracket		= 0;
		int cntSingleQuote		= 0;
		int cntDoubleQuote		= 0;

		for (int i = start; i < end; ++i) {
			char ch = d.getChar(i);

			if (ch == '(') {
				++cntLeftParenthesis;
			}
			else if (ch == '{') {
				++cntLeftBrace;
			}
			else if (ch == '[') {
				++cntLeftBracket;
			}
			else if (ch == ')') {
				++cntRightParenthesis;
			}
			else if (ch == '}') {
				++cntRightBrace;
			}
			else if (ch == ']') {
				++cntRightBracket;
			}
			else if (ch == '\"') {
				++cntDoubleQuote;
			}
			else if (ch == '\'') {
				++cntSingleQuote;
			}
		}

		return  cntLeftParenthesis 	> cntRightParenthesis ||
				cntLeftBrace 		> cntRightBrace ||
				cntLeftBracket		> cntRightBracket ||
				cntDoubleQuote % 2 == 1 ||
				cntSingleQuote % 2 == 1;
	}
}
