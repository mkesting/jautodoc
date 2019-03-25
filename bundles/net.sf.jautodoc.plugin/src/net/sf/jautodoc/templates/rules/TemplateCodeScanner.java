/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.rules;

import java.util.ArrayList;
import java.util.List;

import net.sf.jautodoc.ResourceManager;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;


/**
 * Scanner for template code.
 */
public class TemplateCodeScanner extends RuleBasedScanner {
	private IRulesStrategy[] ruleStrategies;
	
	/**
	 * Instantiates a new template code scanner.
	 * 
	 * @param ruleStrategies the rule strategies
	 */
	public TemplateCodeScanner(IRulesStrategy[] ruleStrategies) {
		super();
		this.ruleStrategies = ruleStrategies;
		initializeRules();
	}

	/**
	 * Gets the document.
	 * 
	 * @return the document
	 */
	public IDocument getDocument() {
		return fDocument;
	}
	
	/**
	 * Creates the rules by using the given rule strategies.
	 * 
	 * @return list of rules
	 */
	private List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		
		// Add generic whitespace rule
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		
		// add rules from strategies
		for (int i = 0; i < ruleStrategies.length; ++i) {
			rules.addAll(ruleStrategies[i].createRules());
		}

		return rules;
	}
	
	/**
	 * Initialize the rules.
	 */
	private void initializeRules() {
		List<IRule> rules = createRules();
		setRules(rules.toArray(new IRule[rules.size()]));
		
		setDefaultReturnToken(ResourceManager.getToken(ResourceManager.DEFAULT));
	}
}
