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

import org.eclipse.jface.text.rules.IRule;

import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.templates.rules.IRulesStrategy;

/**
 * Strategie for Velocity reference rules, where references
 * could be incomplete.
 */
public class StartingReferenceRulesStrategie implements IRulesStrategy {

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.rules.IRulesStrategy#createRules()
	 */
	public List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		
		// Add rules for incomplete references
		rules.add(new ReferenceRule("$!", ResourceManager.getToken(ResourceManager.REFERENCE), true));
		rules.add(new ReferenceRule("$" , ResourceManager.getToken(ResourceManager.REFERENCE), true));
		
		return rules;
	}
}
