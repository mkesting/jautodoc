/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WordRule;


/**
 * Word rule for empty Velocity comments.
 */
public class EmptyCommentRule extends WordRule implements IPredicateRule {

	private IToken successToken;
	
	/**
	 * Instantiates a new empty comment rule.
	 * 
	 * @param successToken the success token
	 */
	public EmptyCommentRule(IToken successToken) {
		super(new EmptyCommentDetector());
		this.successToken= successToken;
		addWord("#**#", successToken); //$NON-NLS-1$
	}

	/*
	 * @see IPredicateRule#evaluate(ICharacterScanner, boolean)
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		return evaluate(scanner);
	}

	/*
	 * @see IPredicateRule#getSuccessToken()
	 */
	public IToken getSuccessToken() {
		return successToken;
	}
}
