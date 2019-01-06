/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.rules;

import java.util.ArrayList;
import java.util.List;

import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.templates.rules.IRulesStrategy;
import net.sf.jautodoc.templates.velocity.contentassist.DirectiveManager;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WordRule;


/**
 * Strategie for Velocity directive rules.
 */
public class DirectiveRulesStrategie implements IRulesStrategy {

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.rules.IRulesStrategy#createRules()
	 */
	public List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		
		// Add word rule for directives
		IToken token = ResourceManager.getToken(ResourceManager.DIRECTIVE);
		WordRule wordRule = new WordRule(new DirectiveDetector(),
				ResourceManager.getToken(ResourceManager.DEFAULT));
		String[] directives = DirectiveManager.getDirectives();
		for (int i = directives.length - 1; i >= 0; i--) {
			wordRule.addWord(directives[i], token);
		}
		rules.add(wordRule);
		
		return rules;
	}
}
