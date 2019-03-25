/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.rules;

import java.util.List;

import org.eclipse.jface.text.rules.IRule;


/**
 * Interface for rule strategies.
 */
public interface IRulesStrategy {
	
	/**
	 * Create rules.
	 * 
	 * @return list of rules
	 */
	public List<IRule> createRules();
}
