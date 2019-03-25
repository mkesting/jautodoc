/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.rules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.templates.replacements.ITemplateReplacementsListener;
import net.sf.jautodoc.templates.replacements.ITemplateReplacementsProvider;
import net.sf.jautodoc.templates.replacements.TemplateReplacementsChangeEvent;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Strategy for replacements/substituions in templates.
 */
public class TemplateReplacementsRulesStrategy implements IRulesStrategy {

	private ITemplateReplacementsProvider replacementsProvider;
	
	
	/**
	 * Instantiates a new template replacements rules strategie.
	 * 
	 * @param replacementsProvider the replacements provider
	 */
	public TemplateReplacementsRulesStrategy(ITemplateReplacementsProvider replacementsProvider) {
		this.replacementsProvider = replacementsProvider;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.rules.IRulesStrategy#createRules()
	 */
	public List<IRule> createRules() {
		List<IRule> rules = new ArrayList<IRule>();
		
		rules.add(new TemplateReplacementsRule());
		
		return rules;
	}

	
	// ----------------------------------------------------
	// inner classes
	// ----------------------------------------------------
	
	/**
	 * Checks for a special word combination.
	 */
	private class WordCombinationRule implements IRule {
		private char[] word;
		private IToken token;
		
		public WordCombinationRule(String word, IToken token) {
			this.word = word.toCharArray();
			this.token = token;
		}
		
		public IToken evaluate(ICharacterScanner scanner) {
			for (int i = 0; i < word.length; i++) {
				int c = scanner.read();
				if (c == ICharacterScanner.EOF) {
					scanner.unread();
					return Token.UNDEFINED;
				} else if (Character.toLowerCase((char)c) != Character.toLowerCase(word[i])) {
					// rewind scanner back to the start.
					for (int j = i; j >= 0; j--) {
						scanner.unread();
					}
					return Token.UNDEFINED;
				}
			}
			
			return token;
		}
	}
	
	/**
	 * Rule for the provided template replacements.
	 */
	private class TemplateReplacementsRule implements IRule, ITemplateReplacementsListener {
		
		private List<IRule> rules = null;

		/* (non-Javadoc)
		 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
		 */
		public IToken evaluate(ICharacterScanner scanner) {
			initRules();
			
			IToken token = Token.UNDEFINED;
			for (int i = 0; i < rules.size() && token == Token.UNDEFINED; ++i) {
				IRule rule = (IRule)rules.get(i);
				token = rule.evaluate(scanner);
			}
			
			return token;
		}
		
		/**
		 * Inits the rules, based on the provided template replacements.
		 */
		private void initRules() {
			if (rules != null) {
				return;
			}
			rules = new ArrayList<IRule>();
			
			IToken token = ResourceManager.getToken(ResourceManager.RESULT);
			
			Iterator<String> iter = replacementsProvider.getReplacements().iterator();
			while (iter.hasNext()) {
				String replacement = ((String)iter.next()).trim();
				if (replacement.length() > 0) {
					rules.add(new WordCombinationRule(replacement, token));
				}
			}
			
			replacementsProvider.addTemplateReplacementsListener(this);
		}

		/* (non-Javadoc)
		 * @see net.sf.jautodoc.preferences.templates.ITemplateResultChangeListener#templateResultChange(net.sf.jautodoc.preferences.templates.TemplateResultChangeEvent)
		 */
		public void templateReplacementsChange(TemplateReplacementsChangeEvent e) {
			rules = null; // reset rules
		}
	}
}
