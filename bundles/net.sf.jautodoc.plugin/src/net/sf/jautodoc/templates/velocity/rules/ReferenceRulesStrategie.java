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

import org.eclipse.jface.text.rules.IRule;

/**
 * Strategie for Velocity reference rules.
 */
public class ReferenceRulesStrategie implements IRulesStrategy {

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.rules.IRulesStrategy#createRules()
	 */
	public List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		
		// Add rules for references
		rules.add(new ReferenceRule("$!", ResourceManager.getToken(ResourceManager.REFERENCE)));
		rules.add(new ReferenceRule("$" , ResourceManager.getToken(ResourceManager.REFERENCE)));
		
		return rules;
	}
}
