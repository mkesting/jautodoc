/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.rules;

import net.sf.jautodoc.utils.ResettableScanner;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;


/**
 * Rule for valid Velocity references.
 */
public class ReferenceRule extends SingleLineRule {
	private static IdentifierDetector detector = new IdentifierDetector();
	
	private ResettableScanner rScanner = new ResettableScanner();
	
	private int parenthesisStack = 0;
	private int braceStack = 0;

	private char lastQuote = ' ';
	private boolean inString = false;
	private boolean seenChar = false;
	
	private boolean allowIncompleteReferences = false;

	private StringBuffer sb = new StringBuffer();


	/**
	 * Instantiates a new reference rule.
	 * 
	 * @param start the start
	 * @param token the token
	 */
	public ReferenceRule(String start, IToken token) {
		this(start, token, false);
	}
	
	/**
	 * Instantiates a new reference rule.
	 * 
	 * @param startSequence the start sequence
	 * @param token the token
	 * @param allowIncompleteReferences true allows incomplete references
	 */
	public ReferenceRule(String startSequence, IToken token, boolean allowIncompleteReferences) {
		super(startSequence, null, token);
		this.allowIncompleteReferences = allowIncompleteReferences;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		rScanner.setScanner(scanner);
		
		parenthesisStack = 0;
		braceStack	= 0;
		lastQuote 	= ' ';
		inString  	= false;
		seenChar 	= false;
		sb.setLength(0);
		
		while (true) {
			int  i = rScanner.read();
			char c = (char)i;
			
			if (i == ICharacterScanner.EOF ||
				c == '\r' || c == '\n') {
				if (isValid(rScanner, true)) return true;
				else break;
			}
			else if (Character.isWhitespace(c)) {
				if (isValid(rScanner, true)) return true;
				else if (!inString && parenthesisStack == 0) break;
			}
			else if (c == '\'' || c == '\"') {
				if (parenthesisStack == 0) break;
				else if (inString) {
					if (lastQuote == c) {
						lastQuote = ' ';
						inString = false;
					}
				}
				else {
					lastQuote = c;
					inString = true;
				}
			}
			else if (c == '(') {
				if (!inString) ++parenthesisStack;
			} else if (c == ')') {
				if (!inString) {
					--parenthesisStack;
					if (parenthesisStack < 0) {
						if (isValid(rScanner, true)) return true;
						else break;
					}
				}
			} else if (c == '{') {
				if (!inString) {
					if (rScanner.getReadCount() == 1) ++braceStack;
					else break;
				}
			} else if (c == '}') {
				if (!inString) {
					--braceStack;
					if (braceStack < 0) break;
					else if (braceStack == 0) {
						if (isValid(rScanner, false)) return true;
						else break;
					}
				}
			}
			else if (c == ',') {
				if (!inString && parenthesisStack == 0) break;
			}
			else {
				if (rScanner.getReadCount() == 1 && braceStack == 0 ||
					rScanner.getReadCount() == 2 && braceStack == 1) {
					if (!detector.isWordStart(c)) break;
					else seenChar = true;
				}
				else if (!seenChar) break;
				else if (!detector.isWordPart(c)) {
					if (isValid(rScanner, true)) return true;
					else break;
				}
			}
			sb.append(c);
		}

		rScanner.reset();
		return false;
	}

	/**
	 * Checks, whether the scanned reference is valid.
	 * 
	 * @param unread true, to unread the last char
	 * @param scanner the scanner
	 * 
	 * @return true, if reference is valid
	 */
	private boolean isValid(ICharacterScanner scanner, boolean unread) {
		if (!allowIncompleteReferences) {
			if (!inString &&
				 seenChar &&
				 braceStack == 0 &&
				 parenthesisStack <= 0) {
				if (unread) scanner.unread();
				return true;
			}
		}
		else {
			if (braceStack >= 0) {
				if (unread) scanner.unread();
				return true;
			}
		}
		
		return false;
	}
}
