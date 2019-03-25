/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.rules;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;


/**
 * Dummy scanner without rules. Deliveres only one token.
 */
public class SingleTokenScanner extends RuleBasedScanner {
	
	/**
	 * Instantiates a new single token scanner.
	 * 
	 * @param token the default token
	 */
	public SingleTokenScanner(IToken token) {
		super();
		setDefaultReturnToken(token);
	}
}
